import { HttpException, HttpStatus, Injectable } from '@nestjs/common';
import { InjectRepository } from '@nestjs/typeorm';
import dateFormat from 'dateformat';
import { Category } from 'entities/Category.entity';
import { Comment } from 'entities/comment.entity';
import { Privilege } from 'entities/role.entity';
import { User } from 'entities/user.entity';
import { UserDTO } from 'modules/user/dto/user.dto';
import { Brackets, Repository } from 'typeorm';
import { Ticket, TicketStatus, ticketStatusToEnum } from '../../entities/ticket.entity';
import { CommentDTO } from './dto/comment.dto';
import { NewCommentDTO } from './dto/newComment.dto';
import { NewTicketDTO } from './dto/newTicket.dto';
import { TicketDTO } from './dto/ticket.dto';

@Injectable()
export class TicketService {
  private _canAccessTicketClause = new Brackets((qb) =>
    qb
      .where(
        `EXISTS (
        SELECT
          1
        FROM
          role_categories
        WHERE
          role_categories."roleId" = role.id
          AND role_categories."categoryId" = category.id
      )`
      )
      .orWhere('role.privilege = :privilege', { privilege: Privilege.ADMINISTRATOR })
  );

  constructor(
    @InjectRepository(Ticket)
    private readonly ticketRepository: Repository<Ticket>,
    @InjectRepository(Comment)
    private readonly commentRepository: Repository<Comment>,
    @InjectRepository(Category)
    private readonly categoryRepository: Repository<Category>,
    @InjectRepository(User)
    private readonly userRepository: Repository<User>
  ) {}

  public async canAccessTicket(roleId: number, ticketId: number): Promise<boolean> {
    const canAccess = await this.ticketRepository
      .createQueryBuilder('ticket')
      .select('ticket.id AS "id"')
      .innerJoin('category', 'category', 'category.id = ticket.categoryId')
      .innerJoin('role', 'role', 'role.id = :roleId', { roleId })
      .andWhere(this._canAccessTicketClause)
      .andWhere('ticket.id = :ticketId', { ticketId })
      .getRawOne();
    return canAccess != null;
  }

  private mapTicketToDTO(ticket: Ticket): TicketDTO {
    if (ticket.category == null)
      throw new HttpException(
        'Expected ticket category to be loaded',
        HttpStatus.INTERNAL_SERVER_ERROR
      );

    return {
      id: ticket.id,
      email: ticket.email,
      date: dateFormat(ticket.dateCreated, 'dddd, mmmm dS yyyy, h:MM:ss TT'),
      title: ticket.title,
      description: ticket.description,
      status: ticket.status,
      categoryId: ticket.category.id,
      categoryName: ticket.category.name,
      categoryColour: ticket.category.colour,
    };
  }

  private async populateCommentsOnDTO(ticket: Ticket): Promise<TicketDTO> {
    const dto = this.mapTicketToDTO(ticket);
    dto.comments = (
      await this.commentRepository
        .createQueryBuilder('comment')
        .select([
          'comment.ticketId AS ticketId',
          'u.id AS commenterId',
          'u.name AS commenterName',
          'comment.date AS commentDate',
          'comment.text as commentText',
        ])
        .leftJoin('user', 'u', 'u.id = comment.userId')
        .where('comment.ticketId = :ticketId', { ticketId: ticket.id })
        .orderBy('comment.date', 'ASC')
        .getRawMany()
    ).map((comment: CommentDTO) => ({
      ...comment,
      commentdate: dateFormat(comment.commentdate, 'dddd, mmmm dS yyyy, h:MM:ss TT'),
    }));
    return dto;
  }

  public async findTickets(roleId: number, filter: string): Promise<TicketDTO[]> {
    let ticketsQuery = this.ticketRepository
      .createQueryBuilder('ticket')
      .innerJoinAndMapOne(
        'ticket.category',
        Category,
        'category',
        'ticket.categoryId = category.id'
      )
      .innerJoin('role', 'role', 'role.id = :roleId', { roleId })
      .where(this._canAccessTicketClause);

    if (filter != null) {
      try {
        ticketsQuery.andWhere('ticket.status = :filter', { filter: ticketStatusToEnum(filter) });
      } catch (err) {
        return [];
      }
    }
    const tickets = await ticketsQuery.getMany();
    if (tickets == null) return [];

    return tickets.map(this.mapTicketToDTO);
  }

  public async findById(ticketId: number): Promise<TicketDTO> {
    const ticket = await this.ticketRepository.findOne(ticketId, { relations: ['category'] });

    if (ticket == null) return null;
    return await this.populateCommentsOnDTO(ticket);
  }

  public async findByToken(token: string): Promise<TicketDTO> {
    const ticket = await this.ticketRepository.findOne({ token }, { relations: ['category'] });

    if (ticket == null) return null;
    return await this.populateCommentsOnDTO(ticket);
  }

  public async createNewTicket(newTicket: NewTicketDTO): Promise<Ticket> {
    const category = await this.categoryRepository.findOne(newTicket.categoryId);
    
    if (category == null) return null;
    const ticket = new Ticket();

    ticket.email = newTicket.email;
    ticket.title = newTicket.title;
    ticket.description = newTicket.description;
    ticket.category = category;

    return await this.ticketRepository.save(ticket);
  }

  public async setTicketStatus(ticketId: number, status: TicketStatus) {
    const ticket = await this.ticketRepository.findOne(ticketId);
    if (
      ticket == null ||
      ticket.status == TicketStatus.LOCKED ||
      (status == TicketStatus.CLOSED &&
        ![TicketStatus.OPEN, TicketStatus.WAITING].includes(ticket.status)) ||
      (status == TicketStatus.OPEN &&
        [TicketStatus.OPEN, TicketStatus.WAITING].includes(ticket.status))
    ) {
      return null;
    }
    ticket.status = status;
    return await this.ticketRepository.save(ticket);
  }

  public async addComment(userDTO: UserDTO, ticketId: number, comment: CommentDTO) {
    const ticket = await this.ticketRepository.findOne(ticketId);
    if (ticket == null || [TicketStatus.CLOSED, TicketStatus.LOCKED].includes(ticket.status))
      return null;
    ticket.status = TicketStatus.WAITING;
    await this.ticketRepository.save(ticket);

    const user = await this.userRepository.findOne(userDTO.id);
    if (user == null) return null;

    const newComment = new Comment();
    newComment.text = comment.commentText;
    newComment.ticket = ticket;
    newComment.user = user;
    return await this.commentRepository.save(newComment);
  }

  public async addCommentFromClient(newCommentDetails: NewCommentDTO) {
    const ticket = await this.ticketRepository.findOne({
      email: newCommentDetails.email,
      token: newCommentDetails.ticketToken,
    });

    if (ticket == null || [TicketStatus.CLOSED, TicketStatus.LOCKED].includes(ticket.status))
      return null;
    ticket.status = TicketStatus.OPEN;
    await this.ticketRepository.save(ticket);

    const newComment = new Comment();
    newComment.text = newCommentDetails.commentText;
    newComment.ticket = ticket;
    newComment.user = null;
    return await this.commentRepository.save(newComment);
  }
}

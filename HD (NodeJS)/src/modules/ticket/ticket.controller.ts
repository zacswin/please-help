import {
  Body,
  Controller,
  HttpException,
  HttpStatus,
  Param,
  ParseIntPipe,
  Post,
  Res,
  UseFilters
} from '@nestjs/common';
import { Privilege } from 'entities/role.entity';
import { TicketStatus } from 'entities/ticket.entity';
import { Response } from 'express';
import { AuthedUser, RequiresPrivilege } from 'lib/authentication';
import { BadFormRequestFilter } from 'lib/BadFormRequestFilter';
import { UserDTO } from 'modules/user/dto/user.dto';
import { CommentDTO } from './dto/comment.dto';
import { TicketService } from './ticket.service';

@Controller('ticket')
export class TicketController {
  constructor(private ticketService: TicketService) {}

  @Post('/:ticketId/comment')
  @UseFilters(BadFormRequestFilter)
  @RequiresPrivilege(Privilege.USER)
  async postComment(
    @AuthedUser() user: UserDTO,
    @Param('ticketId', ParseIntPipe) ticketId: number,
    @Res({ passthrough: true }) res: Response,
    @Body() commentDetails: CommentDTO
  ) {
    if (!(await this.ticketService.canAccessTicket(user.roleId, ticketId)))
      throw new HttpException(
        'You are not in a role who can modify this ticket',
        HttpStatus.UNAUTHORIZED
      );

    const comment = await this.ticketService.addComment(user, ticketId, commentDetails);
    const result = comment == null ? 'failed' : 'success';
    res.redirect('/admin/ticket/' + ticketId + '?commentResult=' + result);
  }

  @Post('/:ticketId/close')
  @UseFilters(BadFormRequestFilter)
  @RequiresPrivilege(Privilege.USER)
  async closeTicket(
    @AuthedUser() user: UserDTO,
    @Param('ticketId', ParseIntPipe) ticketId: number,
    @Res({ passthrough: true }) res: Response
  ) {
    if (!(await this.ticketService.canAccessTicket(user.roleId, ticketId)))
      throw new HttpException(
        'You are not in a role who can modify this ticket',
        HttpStatus.UNAUTHORIZED
      );
    const success = await this.ticketService.setTicketStatus(ticketId, TicketStatus.CLOSED);
    const result = success == null ? 'failed' : 'success';
    res.redirect('/admin/ticket/' + ticketId + '?statusResult=' + result);
  }

  @Post('/:ticketId/re-open')
  @UseFilters(BadFormRequestFilter)
  @RequiresPrivilege(Privilege.USER)
  async reOpenTicket(
    @AuthedUser() user: UserDTO,
    @Param('ticketId', ParseIntPipe) ticketId: number,
    @Res({ passthrough: true }) res: Response
  ) {
    if (!(await this.ticketService.canAccessTicket(user.roleId, ticketId)))
      throw new HttpException(
        'You are not in a role who can modify this ticket',
        HttpStatus.UNAUTHORIZED
      );
    const success = await this.ticketService.setTicketStatus(ticketId, TicketStatus.OPEN);
    const result = success == null ? 'failed' : 'success';
    res.redirect('/admin/ticket/' + ticketId + '?statusResult=' + result);
  }

  @Post('/:ticketId/lock')
  @UseFilters(BadFormRequestFilter)
  @RequiresPrivilege(Privilege.ADMINISTRATOR)
  async lockTicket(
    @AuthedUser() user: UserDTO,
    @Param('ticketId', ParseIntPipe) ticketId: number,
    @Res({ passthrough: true }) res: Response
  ) {
    if (!(await this.ticketService.canAccessTicket(user.roleId, ticketId)))
      throw new HttpException(
        'You are not in a role who can modify this ticket',
        HttpStatus.UNAUTHORIZED
      );
    const success = await this.ticketService.setTicketStatus(ticketId, TicketStatus.LOCKED);
    const result = success == null ? 'failed' : 'success';
    res.redirect('/admin/ticket/' + ticketId + '?statusResult=' + result);
  }
}

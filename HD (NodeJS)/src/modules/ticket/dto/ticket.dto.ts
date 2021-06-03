import { TicketStatus } from 'entities/ticket.entity';
import { CommentDTO } from './comment.dto';

export class TicketDTO {
  id: number;
  email: string;
  date: Date;
  title: string;
  description: string;
  categoryId: number;
  categoryName?: string;
  categoryColour?: string;  
  status: TicketStatus;
  comments?: CommentDTO[];
}

import { IsNotEmpty } from 'class-validator';

export class CommentDTO {
  ticketId: number;

  commenterId: number;

  commenterName: string;

  commentdate: string;

  @IsNotEmpty()
  commentText: string;
}

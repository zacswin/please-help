import { IsEmail, IsNotEmpty } from 'class-validator';

export class NewCommentDTO {
  @IsNotEmpty()
  ticketToken: string;

  @IsNotEmpty()
  commentText: string;

  @IsEmail()
  email: string;
}

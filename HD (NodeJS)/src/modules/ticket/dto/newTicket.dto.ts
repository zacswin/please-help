import { IsEmail } from 'class-validator';

export class NewTicketDTO {
  title: string;
  
  description: string;
  
  @IsEmail()
  email: string;

  categoryId: number;
}

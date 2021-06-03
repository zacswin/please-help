import { IsEmail, IsNotEmpty } from 'class-validator';
import { Match } from 'lib/match.decorator';

export class NewUserDTO {
  id: number;

  @IsNotEmpty()
  name: string;

  @IsNotEmpty()
  password: string;

  @Match('password')
  confirmPassword: string;

  @IsEmail()
  email: string;

  roleId: number;
}

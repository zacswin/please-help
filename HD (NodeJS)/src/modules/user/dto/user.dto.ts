import { IsEmail, IsNotEmpty } from 'class-validator';
import { Privilege } from 'entities/role.entity';

export class UserDTO {
  id: number;

  @IsNotEmpty()
  name: string;

  @IsEmail()
  email: string;

  role: string;

  @IsNotEmpty()
  roleId: number;

  privilege: Privilege;
}

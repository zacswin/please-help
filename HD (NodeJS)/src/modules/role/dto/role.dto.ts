import { IsNotEmpty } from 'class-validator';
import { Privilege } from 'entities/role.entity';

export class RoleDTO {
  id: number;
  @IsNotEmpty()
  name: string;
  privilege: Privilege;
}

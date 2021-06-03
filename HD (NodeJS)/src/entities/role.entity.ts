import { Column, Entity, JoinTable, ManyToMany, OneToMany, PrimaryGeneratedColumn } from 'typeorm';
import { Category } from './category.entity';
import { User } from './user.entity';

export enum Privilege {
  ADMINISTRATOR = 'administrator',
  USER = 'user',
  READONLY = 'readonly',
}

@Entity()
export class Role {
  @PrimaryGeneratedColumn()
  id: number;

  @Column()
  name: string;

  @Column({
    type: 'enum',
    enum: Privilege,
    default: Privilege.USER,
  })
  privilege: Privilege;

  @OneToMany(() => User, (user) => user.role)
  users: User[];

  @ManyToMany(() => Category, category => category.roles)
  categories: Category[];
}

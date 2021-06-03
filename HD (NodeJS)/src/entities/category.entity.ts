import { Column, Entity, JoinTable, ManyToMany, OneToMany, PrimaryGeneratedColumn } from 'typeorm';
import { Role } from './role.entity';
import { Ticket } from './ticket.entity';

@Entity()
export class Category {
  @PrimaryGeneratedColumn()
  id: number;

  @Column()
  name: string;

  @Column()
  colour: string;

  @OneToMany(() => Ticket, (ticket) => ticket.category)
  tickets: Ticket[];

  @ManyToMany(() => Role, (role) => role.categories, { eager: true })
  @JoinTable({
    name: 'role_categories',
    joinColumn: {
      name: 'categoryId',
      referencedColumnName: 'id',
    },
    inverseJoinColumn: {
      name: 'roleId',
      referencedColumnName: 'id',
    },
  })
  roles: Role[];
}

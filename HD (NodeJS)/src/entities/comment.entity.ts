import { Column, CreateDateColumn, Entity, ManyToOne, PrimaryGeneratedColumn } from 'typeorm';
import { Ticket } from './ticket.entity';
import { User } from './user.entity';

@Entity()
export class Comment {
  @PrimaryGeneratedColumn()
  id: number;

  @Column()
  text: string;

  @CreateDateColumn({ type: 'timestamp' })
  date: Date;

  @ManyToOne(() => User, (user) => user.comments, { nullable: true })
  user: User;

  @ManyToOne(() => Ticket, (ticket) => ticket.comments)
  ticket: Ticket;
}

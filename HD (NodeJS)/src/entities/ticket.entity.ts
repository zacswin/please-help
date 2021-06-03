import { timeStamp } from 'node:console';
import {
  BeforeInsert,
  Column,
  CreateDateColumn,
  Entity,
  ManyToOne,
  OneToMany,
  PrimaryGeneratedColumn,
  UpdateDateColumn,
} from 'typeorm';
import { Category } from './category.entity';
import { Comment } from './comment.entity';

export enum TicketStatus {
  OPEN = 'open',
  WAITING = 'waiting',
  CLOSED = 'closed',
  LOCKED = 'locked',
}

export function ticketStatusToEnum(input: string): TicketStatus {
  switch (input) {
    case 'open':
      return TicketStatus.OPEN;
    case 'waiting':
      return TicketStatus.WAITING;
    case 'closed':
      return TicketStatus.CLOSED;
    case 'locked':
      return TicketStatus.LOCKED;
    default:
      throw new Error('Invalid ticket status: ' + input);
  }
}

@Entity()
export class Ticket {
  @PrimaryGeneratedColumn()
  id: number;

  @Column()
  email: string;

  @Column()
  title: string;

  @Column()
  description: string;

  @Column({
    type: 'enum',
    enum: TicketStatus,
    default: TicketStatus.OPEN,
  })
  status: TicketStatus;

  @Column({ generated: 'uuid' })
  token: string;

  @ManyToOne(() => Category, (category) => category.tickets)
  category: Category;

  @OneToMany(() => Comment, (comment) => comment.ticket)
  comments: Comment[];

  @CreateDateColumn({ type: 'timestamp' })
  dateCreated: Date;

  @UpdateDateColumn({ type: 'timestamp' })
  dateUpdated: Date;
}

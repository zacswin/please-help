import { MiddlewareConsumer, Module, NestModule } from '@nestjs/common';
import { TypeOrmModule } from '@nestjs/typeorm';
import { Category } from 'entities/Category.entity';
import { Comment } from 'entities/comment.entity';
import { Ticket } from 'entities/ticket.entity';
import { User } from 'entities/user.entity';
import { AuthenticationMiddleware } from 'lib/authentication.middleware';
import { UserModule } from 'modules/user/user.module';
import { TicketController } from './ticket.controller';
import { TicketService } from './ticket.service';

@Module({
  imports: [TypeOrmModule.forFeature([Ticket, Comment, Category, User]), UserModule],
  providers: [TicketService],
  controllers: [TicketController],
  exports: [TicketService],
})
export class TicketModule implements NestModule {
  configure(consumer: MiddlewareConsumer): void {
    consumer.apply(AuthenticationMiddleware).forRoutes(TicketController);
  }
}

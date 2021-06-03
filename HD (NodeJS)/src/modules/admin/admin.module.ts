import { MiddlewareConsumer, Module, NestModule, RequestMethod } from '@nestjs/common';
import { TypeOrmModule } from '@nestjs/typeorm';
import { Category } from 'entities/category.entity';
import { Comment } from 'entities/comment.entity';
import { Role } from 'entities/role.entity';
import { Ticket } from 'entities/ticket.entity';
import { User } from 'entities/user.entity';
import { CategoryModule } from 'modules/category/category.module';
import { RoleModule } from 'modules/role/role.module';
import { TicketModule } from 'modules/ticket/ticket.module';
import { UserModule } from 'modules/user/user.module';
import { AuthenticationMiddleware } from '../../lib/authentication.middleware';
import { TicketService } from '../ticket/ticket.service';
import { AdminController as AdminController } from './admin.controller';

@Module({
  imports: [
    TypeOrmModule.forFeature([Ticket, Comment, User, Role, Category]),
    UserModule,
    TicketModule,
    CategoryModule,
    RoleModule,
  ],
  providers: [TicketService],
  controllers: [AdminController],
  exports: [],
})
export class AdminModule implements NestModule {
  configure(consumer: MiddlewareConsumer): void {
    consumer.apply(AuthenticationMiddleware).forRoutes(AdminController);
      // {
      //   method: RequestMethod.GET,
      //   path: '/admin/tickets',
      // },
      // {
      //   method: RequestMethod.GET,
      //   path: '/admin/tickets/:ticketId',
      // },
      // {
      //   method: RequestMethod.GET,
      //   path: '/admin/category',
      // },
      // {
      //   method: RequestMethod.GET,
      //   path: '/admin/role',
      // },
      // {
      //   method: RequestMethod.GET,
      //   path: '/admin/user',
      // },
      // {
      //   method: RequestMethod.GET,
      //   path: '/admin/user/:userId',
      // },
      // {
      //   method: RequestMethod.GET,
      //   path: '/admin/user/:userId/delete',
      // },
      // {
      //   method: RequestMethod.GET,
      //   path: '/admin/user/new',
      // },

    // );
  }
}

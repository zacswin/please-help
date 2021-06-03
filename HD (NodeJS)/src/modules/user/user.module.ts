import { MiddlewareConsumer, Module, NestModule, RequestMethod } from '@nestjs/common';
import { TypeOrmModule } from '@nestjs/typeorm';
import { Role } from 'entities/role.entity';
import { User } from 'entities/user.entity';
import { AuthenticationMiddleware } from '../../lib/authentication.middleware';
import { UserController } from './user.controller';
import { UserService } from './user.service';

@Module({
  imports: [TypeOrmModule.forFeature([User, Role])],
  providers: [UserService],
  controllers: [UserController],
  exports: [UserService],
})
export class UserModule implements NestModule {
  configure(consumer: MiddlewareConsumer): void {
    consumer.apply(AuthenticationMiddleware).forRoutes(
      {
        method: RequestMethod.POST,
        path: '/user',
      },
      {
        method: RequestMethod.POST,
        path: '/user/edit',
      },
      {
        method: RequestMethod.POST,
        path: '/user/:userId/delete',
      }
    );
  }
}

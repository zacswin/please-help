import { MiddlewareConsumer, Module, NestModule } from '@nestjs/common';
import { TypeOrmModule } from '@nestjs/typeorm';
import { Role } from 'entities/role.entity';
import { AuthenticationMiddleware } from 'lib/authentication.middleware';
import { UserModule } from 'modules/user/user.module';
import { RoleController } from './role.controller';
import { RoleService } from './role.service';

@Module({
  imports: [TypeOrmModule.forFeature([Role]), UserModule],
  providers: [RoleService],
  controllers: [RoleController],
  exports: [RoleService],
})
export class RoleModule implements NestModule {
  configure(consumer: MiddlewareConsumer): void {
    consumer.apply(AuthenticationMiddleware).forRoutes(RoleController);
  }
}

import { MiddlewareConsumer, Module, NestModule } from '@nestjs/common';
import { TypeOrmModule } from '@nestjs/typeorm';
import { Category } from 'entities/Category.entity';
import { CategoryController } from './category.controller';
import { CategoryService } from './category.service';
import { AuthenticationMiddleware } from 'lib/authentication.middleware';
import { UserModule } from 'modules/user/user.module';
import { Role } from 'entities/role.entity';

@Module({
  imports: [TypeOrmModule.forFeature([Category, Role]), UserModule],
  providers: [CategoryService],
  controllers: [CategoryController],
  exports: [CategoryService],
})
export class CategoryModule implements NestModule {
  configure(consumer: MiddlewareConsumer): void {
    consumer.apply(AuthenticationMiddleware).forRoutes(CategoryController);
  }
}

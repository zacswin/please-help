import { Module } from '@nestjs/common';
import { TypeOrmModule } from '@nestjs/typeorm';
import { config } from 'dotenv';
import { UserModule } from 'modules/user/user.module';
import { AdminModule } from './modules/admin/admin.module';
import { ServeStaticModule } from '@nestjs/serve-static';
import { join } from 'path';
import { AppController } from 'app.controller';
import { CategoryModule } from 'modules/category/category.module';
import { TicketModule } from 'modules/ticket/ticket.module';
import { RoleModule } from 'modules/role/role.module';

config();
const { DB_USERNAME, DB_PASSWORD, DB_DATABASE_NAME, NODE_ENV } = process.env;

@Module({
  imports: [
    TypeOrmModule.forRoot({
      type: 'postgres',
      url: `postgres://${DB_USERNAME}:${DB_PASSWORD}@localhost:5432/${DB_DATABASE_NAME}`,
      autoLoadEntities: true,
      synchronize: true,
      logging: NODE_ENV !== 'production',
    }),
    ServeStaticModule.forRoot({
      rootPath: join(__dirname, '..', 'src', 'static'),
    }),
    CategoryModule,
    AdminModule,
    TicketModule,
    UserModule,
    RoleModule,
  ],
  controllers: [AppController],
})
export class AppModule {}

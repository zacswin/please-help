import { ValidationPipe } from '@nestjs/common';
import { NestFactory } from '@nestjs/core';
import { NestExpressApplication } from '@nestjs/platform-express';
import cookieParser from 'cookie-parser';
import { registerHelper, registerPartials } from 'hbs';
import { join } from 'path';
import { AppModule } from './app.module';

(async () => {
  const app = await NestFactory.create<NestExpressApplication>(AppModule);

  app.use(cookieParser());
  app.useGlobalPipes(new ValidationPipe());
  app.setBaseViewsDir(join(__dirname, '..', 'src/views'));
  app.setViewEngine('hbs');
  app.set('view options', { layout: 'index' });

  registerPartials(join(__dirname, '..', 'src/views/partials'));
  registerHelper('isdefined', function (value) {
    return value != null;
  });
  registerHelper('eq', function (arg1, arg2) {
    return arg1 == arg2;
  });

  registerHelper('inArray', function (arg1, arg2) {
    return arg2.includes(arg1);
  });

  registerHelper('inCommaSeparatedString', function (arg1, arg2) {
    return arg2.split(',').includes(arg1);
  });

  await app.listen(process.env.PORT, async () => {
    console.log('Please Help! running on :90');
  });
})();

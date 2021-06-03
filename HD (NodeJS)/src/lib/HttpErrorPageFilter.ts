import { ArgumentsHost, Catch, ExceptionFilter, HttpException, HttpStatus } from '@nestjs/common';
import { Request, Response } from 'express';

@Catch(HttpException)
export class HttpErrorPageFilter implements ExceptionFilter {
  catch(exception: HttpException, host: ArgumentsHost) {
    const ctx = host.switchToHttp();
    const response = ctx.getResponse<Response>();
    const request = ctx.getRequest<Request>();
    const status = exception.getStatus();
    if (status == HttpStatus.UNAUTHORIZED) {
      console.log('test')
      if (request.user != null) response.render('notAuthorized');
      else response.redirect('/admin');
    }

    if (status == HttpStatus.BAD_REQUEST) response.render('badRequest');
  }
}

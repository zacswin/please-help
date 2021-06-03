import { HttpException, HttpStatus, Injectable, NestMiddleware } from '@nestjs/common';
import { NextFunction, Request, Response } from 'express';
import { verify } from 'jsonwebtoken';
import { UserDTO } from 'modules/user/dto/user.dto';
import { UserService } from 'modules/user/user.service';

@Injectable()
export class AuthenticationMiddleware implements NestMiddleware {
  constructor(private readonly userService: UserService) {}

  async use(req: Request, res: Response, next: NextFunction): Promise<void> {
    try {
      const decoded: any = verify(req.cookies['access-token'], process.env.JWT_SECRET);
      if (!decoded || !decoded.user)
        throw new HttpException('Failed to verify session cookie.', HttpStatus.UNAUTHORIZED);

      const user = await this.userService.findById((decoded.user as UserDTO).id);

      if (!user) throw new HttpException('User not found.', HttpStatus.UNAUTHORIZED);

      req.user = user;
    } catch (err) {
      throw new HttpException('Not authorized.', HttpStatus.UNAUTHORIZED);
    }
    next();
  }
}

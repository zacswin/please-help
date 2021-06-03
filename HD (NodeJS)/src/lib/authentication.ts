import {
  applyDecorators,
  CanActivate,
  createParamDecorator,
  ExecutionContext,
  Injectable,
  SetMetadata,
  UseGuards,
} from '@nestjs/common';
import { Reflector } from '@nestjs/core';
import { Privilege } from 'entities/role.entity';
import { verify } from 'jsonwebtoken';
import { UserDTO } from 'modules/user/dto/user.dto';

function getRoleWeight(privilege: Privilege) {
  switch (privilege) {
    case Privilege.ADMINISTRATOR:
      return 99;
    case Privilege.USER:
      return 1;
    case Privilege.READONLY:
      return 0;
  }
}

export const AuthedUser = createParamDecorator((data: string, ctx: ExecutionContext) => {
  const req = ctx.switchToHttp().getRequest();
  if (!!req.user) {
    return !!data ? req.user[data] : req.user;
  }

  try {
    const decoded: any = verify(req.cookies['access-token'], process.env.JWT_SECRET);
    return !!data ? decoded[data] : decoded.user;
  } catch (err) {}
  return null;
});

@Injectable()
export class RolesGuard implements CanActivate {
  constructor(private reflector: Reflector) {}

  canActivate(context: ExecutionContext): boolean {
    const requiredPrivilege = this.reflector.get<Privilege>('privilege', context.getHandler());
    if (!requiredPrivilege) {
      return true;
    }
    const request = context.switchToHttp().getRequest();
    const user: UserDTO = request.user;
    let canActivate =
      user != null && getRoleWeight(user.privilege) >= getRoleWeight(requiredPrivilege);
    if (!canActivate) context.switchToHttp().getResponse().redirect('/user/login');
    return canActivate;
  }
}

export function RequiresPrivilege(privilege: Privilege = Privilege.READONLY) {
  return applyDecorators(SetMetadata('privilege', privilege), UseGuards(RolesGuard));
}

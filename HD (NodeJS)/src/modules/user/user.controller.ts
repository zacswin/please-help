import { Body, Controller, Get, Param, Post, Query, Render, Res, UseFilters } from '@nestjs/common';
import { Privilege } from 'entities/role.entity';
import { Response } from 'express';
import { RequiresPrivilege } from 'lib/authentication';
import { BadFormRequestFilter } from 'lib/BadFormRequestFilter';
import { NewUserDTO } from './dto/newUser.dto';
import { UserDTO } from './dto/user.dto';
import { UserLoginDTO } from './dto/userLogin.dto';
import { UserService } from './user.service';

@Controller('user')
export class UserController {
  constructor(private userService: UserService) {}

  @Get('/login')
  @Render('login')
  async renderLoginPage(@Query('result') result: string) {
    return { result };
  }

  @Post()
  @RequiresPrivilege(Privilege.ADMINISTRATOR)
  @UseFilters(BadFormRequestFilter)
  async newUser(@Body() newUser: NewUserDTO, @Res({ passthrough: true }) res: Response) {
    const user = await this.userService.newUser(newUser);
    const result = user == null ? 'failed' : 'success';
    res.redirect('/admin/user/new?result=' + result);
  }

  @Post('/edit')
  @RequiresPrivilege(Privilege.ADMINISTRATOR)
  @UseFilters(BadFormRequestFilter)
  async editUser(@Body() userDetails: UserDTO, @Res({ passthrough: true }) res: Response) {
    const user = await this.userService.editUser(userDetails);
    const result = user == null ? 'faileduserDetails' : 'success';
    res.redirect('/admin/user/' + userDetails.id + '?result=' + result);
  }

  @Post('/:userId/delete')
  @RequiresPrivilege(Privilege.ADMINISTRATOR)
  async deleteUser(@Param('userId') userId: string, @Res({ passthrough: true }) res: Response) {
    await this.userService.deleteUser(userId);
    res.redirect('/admin/user');
  }

  @Post('/login')
  async login(@Body() userLogin: UserLoginDTO, @Res({ passthrough: true }) res: Response) {
    // Clear the current request cookie
    res.clearCookie('access-token');

    const user = await this.userService.verifyLogin(userLogin);
    if (user != null)
      return res.cookie('access-token', this.userService.generateJWT(user)).redirect('/admin');
    return res.redirect('/user/login?result=failed');
  }

  @Get('/logout')
  async logout(@Res({ passthrough: true }) res: Response) {
    return res.clearCookie('access-token').redirect('/');
  }
}

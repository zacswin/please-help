import {
  Controller,
  Get,
  HttpException,
  HttpStatus,
  Param,
  ParseIntPipe,
  Query,
  Redirect,
  Render,
  UseFilters,
} from '@nestjs/common';
import { Privilege } from 'entities/role.entity';
import { AuthedUser, RequiresPrivilege } from 'lib/authentication';
import { HttpErrorPageFilter } from 'lib/HttpErrorPageFilter';
import { CategoryService } from 'modules/category/category.service';
import { RoleService } from 'modules/role/role.service';
import { UserDTO } from 'modules/user/dto/user.dto';
import { UserService } from 'modules/user/user.service';
import { TicketService } from '../ticket/ticket.service';

@Controller('admin')
@UseFilters(new HttpErrorPageFilter())
export class AdminController {
  constructor(
    private ticketService: TicketService,
    private userService: UserService,
    private categoryService: CategoryService,
    private roleService: RoleService
  ) {}

  @Get()
  @Redirect('admin/ticket')
  async renderPortal() {}

  @Get('ticket')
  @Render('tickets')
  @RequiresPrivilege()
  async renderTickets(@AuthedUser() user: UserDTO, @Query('filter') filter: string) {
    return { user, filter, tickets: await this.ticketService.findTickets(user.roleId, filter) };
  }

  @Get('ticket/:ticketId')
  @Render('ticket')
  async renderTicket(
    @AuthedUser() user: UserDTO,
    @Param('ticketId', ParseIntPipe) ticketId: number,
    @Query('commentResult') commentResult: string,
    @Query('statusResult') statusResult: string,
    @Query('errors') errors: string
  ) {
    if (!(await this.ticketService.canAccessTicket(user.roleId, ticketId)))
      throw new HttpException(
        'You are not in a role who can view this ticket',
        HttpStatus.UNAUTHORIZED
      );

    return {
      user,
      ticket: await this.ticketService.findById(ticketId),
      commentResult,
      statusResult,
      errors,
    };
  }

  @Get('user')
  @Render('users')
  @RequiresPrivilege(Privilege.ADMINISTRATOR)
  async renderUsers(@AuthedUser() user: UserDTO) {
    const users = await this.userService.findAll();
    return { user, users };
  }

  @Get('user/new')
  @Render('newUser')
  @RequiresPrivilege(Privilege.ADMINISTRATOR)
  async renderNewUser(
    @AuthedUser() user: UserDTO,
    @Query('result') result: string,
    @Query('errors') errors: string
  ) {
    const users = await this.userService.findAll();
    const roles = await this.roleService.findAll();
    return { user, users, roles, result, errors };
  }

  @Get('user/:userId')
  @Render('editUser')
  @RequiresPrivilege(Privilege.ADMINISTRATOR)
  async renderEditUser(
    @AuthedUser() user: UserDTO,
    @Param('userId', ParseIntPipe) userId: number,
    @Query('result') result: string,
    @Query('errors') errors: string
  ) {
    const editUser = await this.userService.findById(userId);
    const roles = await this.roleService.findAll();
    return { user, editUser, roles, result, errors };
  }

  @Get('user/:userId/delete')
  @Render('deleteUser')
  @RequiresPrivilege(Privilege.ADMINISTRATOR)
  async renderDeleteUser(
    @AuthedUser() user: UserDTO,
    @Param('userId', ParseIntPipe) userId: number
  ) {
    return { user, userId };
  }

  @Get('category')
  @Render('categories')
  @RequiresPrivilege(Privilege.ADMINISTRATOR)
  async renderCategories(@AuthedUser() user: UserDTO) {
    const categories = await this.categoryService.findAll();
    return { user, categories };
  }

  @Get('category/new')
  @Render('newCategory')
  @RequiresPrivilege(Privilege.ADMINISTRATOR)
  async renderNewCategory(
    @AuthedUser() user: UserDTO,
    @Query('result') result: string,
    @Query('errors') errors: string
  ) {
    const roles = await this.roleService.findAllForSelectableCategories();
    return { user, roles, result, errors };
  }

  @Get('category/:categoryId')
  @Render('editCategory')
  @RequiresPrivilege(Privilege.ADMINISTRATOR)
  async renderEditCategory(
    @AuthedUser() user: UserDTO,
    @Param('categoryId', ParseIntPipe) categoryId: number,
    @Query('result') result: string,
    @Query('errors') errors: string
  ) {
    const editCategory = await this.categoryService.findById(categoryId);
    const roles = await this.roleService.findAllForSelectableCategories();
    return { user, editCategory, roles, result, errors };
  }

  @Get('category/:categoryId/delete')
  @Render('deleteCategory')
  @RequiresPrivilege(Privilege.ADMINISTRATOR)
  async renderDeleteCategory(
    @AuthedUser() user: UserDTO,
    @Param('categoryId', ParseIntPipe) categoryId: number,
    @Query('result') result: string
  ) {
    return { user, categoryId, result };
  }

  @Get('role')
  @Render('roles')
  @RequiresPrivilege(Privilege.ADMINISTRATOR)
  async renderRoles(@AuthedUser() user: UserDTO) {
    const roles = await this.roleService.findAll();
    return { user, roles };
  }

  @Get('role/new')
  @Render('newRole')
  @RequiresPrivilege(Privilege.ADMINISTRATOR)
  async renderNewRole(
    @AuthedUser() user: UserDTO,
    @Query('result') result: string,
    @Query('errors') errors: string
  ) {
    return { user, result, errors };
  }

  @Get('role/:roleId')
  @Render('editRole')
  @RequiresPrivilege(Privilege.ADMINISTRATOR)
  async renderEditRole(
    @AuthedUser() user: UserDTO,
    @Param('roleId', ParseIntPipe) roleId: number,
    @Query('result') result: string,
    @Query('errors') errors: string
  ) {
    const editRole = await this.roleService.findById(roleId);
    return { user, editRole, result, errors };
  }

  @Get('role/:roleId/delete')
  @Render('deleteRole')
  @RequiresPrivilege(Privilege.ADMINISTRATOR)
  async renderDeleteRole(
    @AuthedUser() user: UserDTO,
    @Param('roleId', ParseIntPipe) roleId: number,
    @Query('result') result: string
  ) {
    return { user, roleId, result };
  }
}

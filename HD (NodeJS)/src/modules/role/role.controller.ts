import { Body, Controller, Param, Post, Res, UseFilters } from '@nestjs/common';
import { Privilege } from 'entities/role.entity';
import { Response } from 'express';
import { RequiresPrivilege } from 'lib/authentication';
import { BadFormRequestFilter } from 'lib/BadFormRequestFilter';
import { RoleDTO } from './dto/role.dto';
import { RoleService } from './role.service';

@Controller('role')
export class RoleController {
  constructor(private roleService: RoleService) {}

  @Post()
  @RequiresPrivilege(Privilege.ADMINISTRATOR)
  @UseFilters(BadFormRequestFilter)
  async newRole(@Body() newRole: RoleDTO, @Res({ passthrough: true }) res: Response) {
    const role = await this.roleService.newRole(newRole);
    const result = role == null ? 'failed' : 'success';
    res.redirect('/admin/role/new?result=' + result);
  }

  @Post('/edit')
  @RequiresPrivilege(Privilege.ADMINISTRATOR)
  @UseFilters(BadFormRequestFilter)
  async editRole(@Body() roleDetails: RoleDTO, @Res({ passthrough: true }) res: Response) {
    const role = await this.roleService.editRole(roleDetails);
    const result = role == null ? 'failed' : 'success';
    res.redirect('/admin/role/' + roleDetails.id + '?result=' + result);
  }

  @Post('/:roleId/delete')
  @RequiresPrivilege(Privilege.ADMINISTRATOR)
  async deleteRole(@Param('roleId') roleId: string, @Res({ passthrough: true }) res: Response) {
    try {
      await this.roleService.deleteRole(roleId);
    } catch (err) {
      res.redirect('/admin/role/' + roleId + '/delete?result=failed');
    }
    res.redirect('/admin/role');
  }
}

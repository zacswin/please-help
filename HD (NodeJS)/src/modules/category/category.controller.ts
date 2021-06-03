import { Body, Controller, Param, Post, Res, UseFilters } from '@nestjs/common';
import { Privilege } from 'entities/role.entity';
import { RequiresPrivilege } from 'lib/authentication';
import { BadFormRequestFilter } from 'lib/BadFormRequestFilter';
import { CategoryService } from './category.service';
import { CategoryDTO } from './dto/category.dto';
import { Response } from 'express';

@Controller('category')
export class CategoryController {
  constructor(private categoryService: CategoryService) {}

  @Post()
  @RequiresPrivilege(Privilege.ADMINISTRATOR)
  @UseFilters(BadFormRequestFilter)
  async newCategory(@Body() newCategory: CategoryDTO, @Res({ passthrough: true }) res: Response) {
    const category = await this.categoryService.newCategory(newCategory);
    const result = category == null ? 'failed' : 'success';
    res.redirect('/admin/category/new?result=' + result);
  }

  @Post('/edit')
  @RequiresPrivilege(Privilege.ADMINISTRATOR)
  @UseFilters(BadFormRequestFilter)
  async editCategory(
    @Body() categoryDetails: CategoryDTO,
    @Res({ passthrough: true }) res: Response
  ) {
    const category = await this.categoryService.editCategory(categoryDetails);
    const result = category == null ? 'failed' : 'success';
    res.redirect('/admin/category/' + categoryDetails.id + '?result=' + result);
  }

  @Post('/:categoryId/delete')
  @RequiresPrivilege(Privilege.ADMINISTRATOR)
  async deleteCategory(@Param('categoryId') categoryId: string, @Res({ passthrough: true }) res: Response, ) {
    try {
      await this.categoryService.deleteCategory(categoryId);
    } catch(err) {
      res.redirect('/admin/category/' + categoryId + '/delete?result=failed');
    }
    res.redirect('/admin/category');
  }
}

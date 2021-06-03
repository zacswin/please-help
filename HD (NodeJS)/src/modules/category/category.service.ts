import { Injectable } from '@nestjs/common';
import { InjectRepository } from '@nestjs/typeorm';
import { Role } from 'entities/role.entity';
import { Repository } from 'typeorm';
import { Category } from '../../entities/Category.entity';
import { CategoryDTO } from './dto/category.dto';

@Injectable()
export class CategoryService {
  constructor(
    @InjectRepository(Category)
    private readonly categoryRepository: Repository<Category>,
    @InjectRepository(Role)
    private readonly roleRepository: Repository<Role>
  ) {}

  public async findAll(): Promise<CategoryDTO[]> {
    return (await this.categoryRepository.find()).map(this.mapToDTO);
  }

  private mapToDTO(category: Category): CategoryDTO {
    return {
      id: category.id,
      name: category.name,
      colour: category.colour,
      roles: category.roles.map((role) => role.id),
    };
  }

  private categoryFromDTO(categoryDTO: CategoryDTO): Promise<Category> {
    return this.categoryRepository.findOne(categoryDTO.id);
  }

  async findById(id: number): Promise<CategoryDTO> {
    const category = await this.categoryRepository.findOne(id);
    if (category == null) return null;

    return this.mapToDTO(category);
  }

  public async newCategory(newCategory: CategoryDTO) {
    const category = new Category();
    category.name = newCategory.name;
    category.colour = newCategory.colour;

    await this.categoryRepository.save(category);

    await this.categoryRepository
      .createQueryBuilder()
      .relation(Category, 'roles')
      .of(category.id)
      .add(newCategory.roles);

    return category;
  }

  public async editCategory(categoryDetails: CategoryDTO) {
    const category = await this.categoryFromDTO(categoryDetails);

    category.name = categoryDetails.name;
    category.colour = categoryDetails.colour;

    await this.categoryRepository.save(category);

    const currentRoles = await this.roleRepository.findByIds(category.roles);

    // Remove existing role assignments
    await this.categoryRepository
      .createQueryBuilder()
      .relation(Category, 'roles')
      .of(categoryDetails.id)
      .remove(currentRoles);

    // Add new role assignments
    await this.categoryRepository
      .createQueryBuilder()
      .relation(Category, 'roles')
      .of(categoryDetails.id)
      .add(categoryDetails.roles);

    return category;
  }

  public async deleteCategory(categoryId: string) {
    await this.categoryRepository.delete(categoryId);
  }
}

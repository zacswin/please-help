import { Injectable } from '@nestjs/common';
import { InjectRepository } from '@nestjs/typeorm';
import { Privilege, Role } from 'entities/role.entity';
import { Repository } from 'typeorm';
import { RoleDTO } from './dto/role.dto';

@Injectable()
export class RoleService {
  constructor(
    @InjectRepository(Role)
    private readonly roleRepository: Repository<Role>
  ) {}

  private mapToDTO(role: Role): RoleDTO {
    return {
      id: role.id,
      name: role.name,
      privilege: role.privilege,
    };
  }

  public async findAll(): Promise<RoleDTO[]> {
    return (await this.roleRepository.find()).map(this.mapToDTO);
  }

  public async findAllForSelectableCategories(): Promise<RoleDTO[]> {
    return (
      await this.roleRepository.find({
        where: [{ privilege: Privilege.USER }, { privilege: Privilege.READONLY }],
      })
    ).map(this.mapToDTO);
  }

  private roleFromDTO(categoryDTO: RoleDTO): Promise<Role> {
    return this.roleRepository.findOne(categoryDTO.id);
  }

  async findById(id: number): Promise<RoleDTO> {
    const role = await this.roleRepository.findOne(id);
    if (role == null) return null;

    return this.mapToDTO(role);
  }

  public async newRole(newRole: RoleDTO) {
    const role = new Role();
    role.name = newRole.name;
    role.privilege = newRole.privilege;

    return await this.roleRepository.save(role);
  }

  public async editRole(roleDetails: RoleDTO) {
    const role = await this.roleFromDTO(roleDetails);

    role.name = roleDetails.name;
    role.privilege = roleDetails.privilege;

    return this.roleRepository.save(role);
  }

  public async deleteRole(roleId: string) {
    await this.roleRepository.delete(roleId);
  }
}

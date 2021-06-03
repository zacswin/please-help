import { Injectable } from '@nestjs/common';
import { InjectRepository } from '@nestjs/typeorm';
import { compare } from 'bcrypt';
import { Role } from 'entities/role.entity';
import { User } from 'entities/user.entity';
import { sign } from 'jsonwebtoken';
import { Repository } from 'typeorm';
import { NewUserDTO } from './dto/newUser.dto';
import { UserDTO } from './dto/user.dto';
import { UserLoginDTO } from './dto/userLogin.dto';

@Injectable()
export class UserService {
  constructor(
    @InjectRepository(User)
    private readonly userRepository: Repository<User>,
    @InjectRepository(Role)
    private readonly roleRepository: Repository<Role>
  ) {}

  private mapToDTO(user: User): UserDTO {
    return {
      id: user.id,
      name: user.name,
      email: user.email,
      role: user.role.name,
      privilege: user.role.privilege,
      roleId: user.role.id,
    };
  }

  private userFromDTO(userDTO: UserDTO): Promise<User> {
    return this.userRepository.findOne(userDTO.id);
  }

  public async findAll(): Promise<UserDTO[]> {
    return (await this.userRepository.find()).map(this.mapToDTO);
  }

  public async findById(id: number): Promise<UserDTO> {
    const user = await this.userRepository.findOne(id);
    if (user == null) return null;

    return this.mapToDTO(user);
  }

  public async verifyLogin(userLogin: UserLoginDTO) {
    const user = await this.userRepository.findOne({
      where: { email: userLogin.email },
    });
    if (user == null) return null;
    if (await compare(userLogin.password, user.password)) return this.mapToDTO(user);
    return null;
  }

  public generateJWT(user: UserDTO): string {
    const today = new Date();
    const exp = new Date(today);
    exp.setDate(today.getDate() + 60);

    return sign(
      {
        user,
        exp: exp.getTime() / 1000,
      },
      process.env.JWT_SECRET
    );
  }

  public async newUser(newUser: NewUserDTO) {
    const role = await this.roleRepository.findOne(newUser.roleId);
    const user = new User();
    user.name = newUser.name;
    user.email = newUser.email;
    user.password = newUser.password;
    user.role = role;

    return await this.userRepository.save(user);
  }

  public async editUser(userDetails: UserDTO) {
    const role = await this.roleRepository.findOne(userDetails.roleId);

    const user = await this.userFromDTO(userDetails);
    user.email = userDetails.email;
    user.name = userDetails.name;
    user.role = role;

    return await this.userRepository.save(user);
  }

  public async deleteUser(userId: string) {
    await this.userRepository.delete(userId);
  }
}

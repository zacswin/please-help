import { ArrayMinSize, IsNotEmpty, Length } from 'class-validator';

export class CategoryDTO {
  id: number;

  @IsNotEmpty()
  name: string;

  colour: string;
  
  @ArrayMinSize(1)
  roles: number[];
}

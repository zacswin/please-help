import {
  Body,
  Controller,
  Get,
  Param,
  ParseUUIDPipe,
  Post,
  Query,
  Render,
  Res,
  UseFilters,
} from '@nestjs/common';
import { Response } from 'express';
import { BadFormRequestFilter } from 'lib/BadFormRequestFilter';
import { HttpErrorPageFilter } from 'lib/HttpErrorPageFilter';
import { CategoryService } from 'modules/category/category.service';
import { NewCommentDTO } from 'modules/ticket/dto/newComment.dto';
import { NewTicketDTO } from 'modules/ticket/dto/newTicket.dto';
import { TicketService } from 'modules/ticket/ticket.service';

@Controller('/')
@UseFilters(new HttpErrorPageFilter())
export class AppController {
  constructor(private categoryService: CategoryService, private ticketService: TicketService) {}

  @Get()
  @Render('submitTicket')
  async renderSubmitTicketForm(@Query('result') result: string) {
    const categories = await this.categoryService.findAll();
    return { categories, result };
  }

  @Post('/submit-ticket')
  async submitNewTicket(
    @Body() newTicket: NewTicketDTO,
    @Res({ passthrough: true }) res: Response
  ) {
    const ticket = await this.ticketService.createNewTicket(newTicket);
    const result = ticket == null ? 'failed' : 'success';
    res.redirect('/?result=' + result);
  }

  @Get('ticket/:ticketToken')
  @Render('respondToTicket')
  async renderRespondToTicket(
    @Param('ticketToken', ParseUUIDPipe) ticketToken: string,
    @Query('errors') errors: string
  ) {
    const ticket = await this.ticketService.findByToken(ticketToken);
    return { ticket, errors, ticketToken };
  }

  @Post('new-comment')
  @UseFilters(BadFormRequestFilter)
  async postRespondToTicket(
    @Res({ passthrough: true }) res: Response,
    @Body() commentDetails: NewCommentDTO
  ) {
    const result = await this.ticketService.addCommentFromClient(commentDetails);
    if (result == null)
      res.redirect('/ticket/' + commentDetails.ticketToken + '?errors=Incorrect email provided');
    else res.redirect('/ticket/' + commentDetails.ticketToken);
  }
}

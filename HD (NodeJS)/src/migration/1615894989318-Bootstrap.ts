import { MigrationInterface, QueryRunner } from 'typeorm';

const DUMMY_TEXT = 'Lorem ipsum dolor sit amet, consectetur adipiscing elit. Pellentesque sit amet augue erat. Aliquam porta tellus magna, sit amet porta ex gravida a. Aliquam quis nunc non nulla iaculis congue pulvinar nec nibh. Phasellus dignissim, enim ullamcorper finibus elementum, ligula nibh scelerisque diam, eget luctus turpis libero condimentum diam. Vivamus et cursus nibh. Lorem ipsum dolor sit amet, consectetur adipiscing elit. Proin malesuada vehicula arcu sit amet lobortis. Sed porttitor ut nulla at condimentum.';

export class Bootstrap1615894989318 implements MigrationInterface {
  public async up(queryRunner: QueryRunner): Promise<void> {
    await queryRunner.query(`
      INSERT INTO "role"
        ("id", "name", "privilege")
      VALUES
        (0, 'Administrator', 'administrator'),
        (1, 'Training', 'readonly'),
        (2, 'Marketing', 'user'),
        (3, 'Accounting', 'user'),
        (4, 'IT Support', 'user')
    `);
    await queryRunner.query(`
      INSERT INTO "user"
        ("id", "name", "email", "password", "roleId")
      VALUES
        (0, 'System Administrator', 'admin@please-help.com', '$2b$10$2mg9iOByDF0wvIyzXUirTOhRGR9fWwR5VlzE3ayAAJiogJWrHcWKu', 0),
        (1, 'Newbie Trainee', 'newbie@please-help.com', '$2b$10$2mg9iOByDF0wvIyzXUirTOhRGR9fWwR5VlzE3ayAAJiogJWrHcWKu', 1),
        (2, 'John Smith', 'jsmith@please-help.com', '$2b$10$2mg9iOByDF0wvIyzXUirTOhRGR9fWwR5VlzE3ayAAJiogJWrHcWKu', 3)
    `);
    await queryRunner.query(`
      INSERT INTO "category"
        ("id", "name", "colour")
      VALUES
        (0, 'Account Issue', 'primary'),
        (1, 'Payment & Refunds', 'success'),
        (2, 'Bug Report', 'danger'),
        (3, 'Website Help', 'warning')
    `);

    await queryRunner.query(`
      INSERT INTO "ticket"
        ("id", "title", "status", "categoryId", "description", "email")
      VALUES
        (0, 'I forgot my password', 'waiting', 0, '${DUMMY_TEXT}', 'testemail1@test.com'),
        (1, 'A button broke on the website', 'closed', 2, '${DUMMY_TEXT}', 'testemail2@test.com'),
        (2, 'REFUND ME NOW!!!', 'locked', 1, '${DUMMY_TEXT}', 'testemail3@test.com'),
        (3, 'How do I find my orders?', 'open', 3, '${DUMMY_TEXT}', 'testemail4@test.com')
    `);

    await queryRunner.query(`
      INSERT INTO "comment"
        ("id", "text", "date", "ticketId", "userId")
      VALUES
        (0, 'Hey could you please send a screenshot of the issue?', CURRENT_TIMESTAMP, 1, 0),
        (1, 'Sure, here you go: http://google.com/', CURRENT_TIMESTAMP + interval '1 hour', 1, null),
        (2, 'Thanks for letting us know!', CURRENT_TIMESTAMP + interval '2 hour', 1, 0),
        (3, 'Here is the link to reset it: http://google.com/', CURRENT_TIMESTAMP + interval '1 hour', 0, 2)
    `);
  }

  public async down(queryRunner: QueryRunner): Promise<void> {
    await queryRunner.query('TRUNCATE "role" CASCADE');
    await queryRunner.query('TRUNCATE "category" CASCADE');
    await queryRunner.query('TRUNCATE "ticket" CASCADE');
  }
}

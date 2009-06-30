package liquibase.sqlgenerator.core;

import liquibase.database.Database;
import liquibase.database.core.InformixDatabase;
import liquibase.database.core.SQLiteDatabase;
import liquibase.exception.ValidationErrors;
import liquibase.sql.Sql;
import liquibase.sql.UnparsedSql;
import liquibase.statement.core.AddForeignKeyConstraintStatement;
import liquibase.sqlgenerator.SqlGenerator;
import liquibase.sqlgenerator.SqlGeneratorChain;

public class AddForeignKeyConstraintGenerator implements SqlGenerator<AddForeignKeyConstraintStatement> {
    public int getPriority() {
        return PRIORITY_DEFAULT;
    }

    public boolean supports(AddForeignKeyConstraintStatement statement, Database database) {
        return (!(database instanceof SQLiteDatabase));
    }

    public ValidationErrors validate(AddForeignKeyConstraintStatement addForeignKeyConstraintStatement, Database database, SqlGeneratorChain sqlGeneratorChain) {
        ValidationErrors validationErrors = new ValidationErrors();

        if (!database.supportsInitiallyDeferrableColumns()) {
            validationErrors.checkDisallowedField("initiallyDeferred", addForeignKeyConstraintStatement.isInitiallyDeferred());
            validationErrors.checkDisallowedField("deferrable", addForeignKeyConstraintStatement.isDeferrable());
        }

        validationErrors.checkRequiredField("baseColumnNames", addForeignKeyConstraintStatement.getBaseColumnNames());
        validationErrors.checkRequiredField("baseTableNames", addForeignKeyConstraintStatement.getBaseTableName());
        validationErrors.checkRequiredField("referencedColumnNames", addForeignKeyConstraintStatement.getReferencedColumnNames());
        validationErrors.checkRequiredField("referencedTableName", addForeignKeyConstraintStatement.getReferencedTableName());

        return validationErrors;
    }

    public Sql[] generateSql(AddForeignKeyConstraintStatement statement, Database database, SqlGeneratorChain sqlGeneratorChain) {
        StringBuilder sb = new StringBuilder();
        sb.append("ALTER TABLE ")
        	.append(database.escapeTableName(statement.getBaseTableSchemaName(), statement.getBaseTableName()))
        	.append(" ADD CONSTRAINT ");
        if (!(database instanceof InformixDatabase)) {
        	sb.append(database.escapeConstraintName(statement.getConstraintName()));
        }
        sb.append(" FOREIGN KEY (")
        	.append(database.escapeColumnNameList(statement.getBaseColumnNames()))
        	.append(") REFERENCES ")
        	.append(database.escapeTableName(statement.getReferencedTableSchemaName(), statement.getReferencedTableName()))
        	.append("(")
        	.append(database.escapeColumnNameList(statement.getReferencedColumnNames()))
        	.append(")");

        if (statement.getOnUpdate() != null) {
            sb.append(" ON UPDATE ").append(statement.getOnUpdate());
        }

        if (statement.getOnDelete() != null) {
            sb.append(" ON DELETE ").append(statement.getOnDelete());
        }

        if (statement.isDeferrable() || statement.isInitiallyDeferred()) {
            if (statement.isDeferrable()) {
            	sb.append(" DEFERRABLE");
            }

            if (statement.isInitiallyDeferred()) {
            	sb.append(" INITIALLY DEFERRED");
            }
        }

        if (database instanceof InformixDatabase) {
        	sb.append(" CONSTRAINT ");
        	sb.append(database.escapeConstraintName(statement.getConstraintName()));
        }

        return new Sql[] {
                new UnparsedSql(sb.toString())
        };
    }
}

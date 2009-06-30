package liquibase.change.core;

import liquibase.database.Database;
import liquibase.statement.core.AddForeignKeyConstraintStatement;
import liquibase.statement.SqlStatement;
import liquibase.change.AbstractChange;
import liquibase.change.ChangeMetaData;
import liquibase.change.Change;
import liquibase.exception.UnexpectedLiquibaseException;

import java.sql.DatabaseMetaData;

/**
 * Adds a foreign key constraint to an existing column.
 */
public class AddForeignKeyConstraintChange extends AbstractChange {
    private String baseTableSchemaName;
    private String baseTableName;
    private String baseColumnNames;

    private String referencedTableSchemaName;
    private String referencedTableName;
    private String referencedColumnNames;

    private String constraintName;

    private Boolean deferrable;
    private Boolean initiallyDeferred;

    private String onUpdate;
    private String onDelete;

    public AddForeignKeyConstraintChange() {
        super("addForeignKeyConstraint", "Add Foreign Key Constraint", ChangeMetaData.PRIORITY_DEFAULT);
    }

    public String getBaseTableSchemaName() {
        return baseTableSchemaName;
    }

    public void setBaseTableSchemaName(String baseTableSchemaName) {
        this.baseTableSchemaName = baseTableSchemaName;
    }

    public String getBaseTableName() {
        return baseTableName;
    }

    public void setBaseTableName(String baseTableName) {
        this.baseTableName = baseTableName;
    }

    public String getBaseColumnNames() {
        return baseColumnNames;
    }

    public void setBaseColumnNames(String baseColumnNames) {
        this.baseColumnNames = baseColumnNames;
    }

    public String getReferencedTableSchemaName() {
        return referencedTableSchemaName;
    }

    public void setReferencedTableSchemaName(String referencedTableSchemaName) {
        this.referencedTableSchemaName = referencedTableSchemaName;
    }

    public String getReferencedTableName() {
        return referencedTableName;
    }

    public void setReferencedTableName(String referencedTableName) {
        this.referencedTableName = referencedTableName;
    }

    public String getReferencedColumnNames() {
        return referencedColumnNames;
    }

    public void setReferencedColumnNames(String referencedColumnNames) {
        this.referencedColumnNames = referencedColumnNames;
    }

    public String getConstraintName() {
        return constraintName;
    }

    public void setConstraintName(String constraintName) {
        this.constraintName = constraintName;
    }

    public Boolean getDeferrable() {
        return deferrable;
    }

    public void setDeferrable(Boolean deferrable) {
        this.deferrable = deferrable;
    }

    public Boolean getInitiallyDeferred() {
        return initiallyDeferred;
    }

    public void setInitiallyDeferred(Boolean initiallyDeferred) {
        this.initiallyDeferred = initiallyDeferred;
    }

//    public Boolean getDeleteCascade() {
//        return deleteCascade;
//    }

    public void setDeleteCascade(Boolean deleteCascade) {
        if (deleteCascade != null && deleteCascade) {
            setOnDelete("CASCADE");
        }
    }

    public void setOnUpdate(String rule) {
        this.onUpdate = rule;
    }

    public String getOnUpdate() {
        return onUpdate;
    }

    public void setOnDelete(String onDelete) {
        this.onDelete = onDelete;
    }

    public String getOnDelete() {
        return this.onDelete;
    }

    public void setOnDelete(int rule) {
        if (rule == DatabaseMetaData.importedKeyCascade) {
            setOnDelete("CASCADE");
        } else if (rule == DatabaseMetaData.importedKeySetNull) {
            setOnDelete("SET NULL");
        } else if (rule == DatabaseMetaData.importedKeySetDefault) {
            setOnDelete("SET DEFAULT");
        } else if (rule == DatabaseMetaData.importedKeyRestrict) {
            setOnDelete("RESTRICT");
        } else if (rule == DatabaseMetaData.importedKeyNoAction){
            setOnDelete("NO ACTION");
        } else {
            throw new UnexpectedLiquibaseException("Unknown onDelete action: "+rule);
        }
    }

    public void setOnUpdate(int rule) {
        if (rule == DatabaseMetaData.importedKeyCascade) {
            setOnUpdate("CASCADE");
        } else  if (rule == DatabaseMetaData.importedKeySetNull) {
            setOnUpdate("SET NULL");
        } else if (rule == DatabaseMetaData.importedKeySetDefault) {
            setOnUpdate("SET DEFAULT");
        } else if (rule == DatabaseMetaData.importedKeyRestrict) {
            setOnUpdate("RESTRICT");
        } else if (rule == DatabaseMetaData.importedKeyNoAction) {
            setOnUpdate("NO ACTION");
        } else {
            throw new UnexpectedLiquibaseException("Unknown onUpdate action: "+onUpdate);
        }
    }

    public SqlStatement[] generateStatements(Database database) {

        boolean deferrable = false;
        if (getDeferrable() != null) {
            deferrable = getDeferrable();
        }

        boolean initiallyDeferred = false;
        if (getInitiallyDeferred() != null) {
            initiallyDeferred = getInitiallyDeferred();
        }

        return new SqlStatement[]{
                new AddForeignKeyConstraintStatement(getConstraintName(),
                        getBaseTableSchemaName() == null ? database.getDefaultSchemaName() : getBaseTableSchemaName(),
                        getBaseTableName(),
                        getBaseColumnNames(),
                        getReferencedTableSchemaName() == null ? database.getDefaultSchemaName() : getReferencedTableSchemaName(),
                        getReferencedTableName(),
                        getReferencedColumnNames())
                        .setDeferrable(deferrable)
                        .setInitiallyDeferred(initiallyDeferred)
                        .setOnUpdate(getOnUpdate())
                        .setOnDelete(getOnDelete())
        };
    }

    @Override
    protected Change[] createInverses() {
        DropForeignKeyConstraintChange inverse = new DropForeignKeyConstraintChange();
        inverse.setBaseTableSchemaName(getBaseTableSchemaName());
        inverse.setBaseTableName(getBaseTableName());
        inverse.setConstraintName(getConstraintName());

        return new Change[]{
                inverse
        };
    }

    public String getConfirmationMessage() {
        return "Foreign key contraint added to " + getBaseTableName() + " (" + getBaseColumnNames() + ")";
    }
}

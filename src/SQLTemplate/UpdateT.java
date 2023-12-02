package SQLTemplate;

public class UpdateT extends SQLT{
    public StringBuffer updateSQL;

    public UpdateT(String table) {
        this.table = table;
        this.updateSQL = new StringBuffer("UPDATE ").append(table);
    }

    @Override
    public String toSQL() {
        this.updateSQL.append(" SET ");
        for (int i = 0; i < keys.size(); i ++) {
            this.updateSQL.append(this.keys.get(i)).append(" = ").append(this.values.get(i));
            if (i < keys.size()-1) {
                this.updateSQL.append(", ");
            }
        }
        if (! conditions.isEmpty()) {
            this.updateSQL.append(" WHERE ");
            for (int i = 0; i < conditions.size(); i ++) {
                this.updateSQL.append(conditions.get(i));
                if (i < conditions.size()-1) {
                    this.updateSQL.append(" AND ");
                }
            }
        }
        return this.updateSQL.append(";").toString();
    }

}

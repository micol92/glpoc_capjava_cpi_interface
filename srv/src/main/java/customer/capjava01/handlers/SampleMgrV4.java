package customer.capjava01.handlers;

//package com.sap.cap.bookstore_java.handlers.xx;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import com.sap.cds.services.cds.CdsService;
import com.sap.cds.services.handler.EventHandler;
import com.sap.cds.services.handler.annotations.On;
import com.sap.cds.services.handler.annotations.ServiceName;
import org.springframework.jdbc.core.SqlParameter;
import org.springframework.jdbc.core.SqlReturnResultSet;
import org.springframework.jdbc.core.CallableStatementCreator;

import cds.gen.samplemgrv4service.*;

@Component
@ServiceName(SampleMgrV4Service_.CDS_NAME)
public class SampleMgrV4 implements EventHandler {

    @Autowired
    private JdbcTemplate jdbc;

    @Autowired
    @Qualifier(SampleMgrV4Service_.CDS_NAME)
    private CdsService SampleMgrV4Service;

    // Procedure Call - header Insert
    // Header Multi Row
    /*********************************
    {
        "sampleHeaders" : [
            {"header_id" : 106, "cd": "eeee11", "name": "eeee11"},
            {"header_id" : 107, "cd": "eeee12", "name": "eeee12"}
        ]
    }
    *********************************/


    @Transactional(rollbackFor = SQLException.class)
    @On(event = SaveSampleHeaderMultiProcContext.CDS_NAME)
    public void onSaveSampleHeaderMultiProc(SaveSampleHeaderMultiProcContext context) {

        String v_sql_commitOption = "SET TRANSACTION AUTOCOMMIT DDL OFF;";
        String v_sql_createTable = "CREATE local TEMPORARY column TABLE #LOCAL_TEMP (HEADER_ID BIGINT, CD NVARCHAR(5000), NAME NVARCHAR(5000))";
        String v_sql_dropable = "DROP TABLE #LOCAL_TEMP";
        String v_sql_insertTable = "INSERT INTO #LOCAL_TEMP VALUES (?, ?, ?)";
        String v_sql_callProc = "CALL SAMPLE_HEADER_SAVE_PROC(I_TABLE => #LOCAL_TEMP, O_TABLE => ?)";

        Collection<SavedHeadersOutput> v_result = new ArrayList<>();
        Collection<SavedHeaders> v_inHeaders = context.getSampleHeaders();

        // Commit Option
        jdbc.execute(v_sql_commitOption);

        // Create Local Temp Table 
        jdbc.execute(v_sql_createTable);
    
        // Insert Local Temp Table
        List<Object[]> batch = new ArrayList<Object[]>();
        if(!v_inHeaders.isEmpty() && v_inHeaders.size() > 0){
            for(SavedHeaders v_inRow : v_inHeaders){
                Object[] values = new Object[] {
                    v_inRow.get("header_id"),
                    v_inRow.get("cd"),
                    v_inRow.get("name")};
                batch.add(values);
            }
        }

        int[] updateCounts = jdbc.batchUpdate(v_sql_insertTable, batch);
       
        // Procedure Call
        SqlReturnResultSet oTable = new SqlReturnResultSet("O_TABLE", new RowMapper<SavedHeadersOutput>(){
            @Override
            public SavedHeadersOutput mapRow(ResultSet v_rs, int rowNum) throws SQLException {
                SavedHeadersOutput v_row = SavedHeadersOutput.create();
                v_row.setHeaderId(v_rs.getLong("header_id"));
                v_row.setHeaderSeq(v_rs.getLong("header_seq"));
                v_row.setCd(v_rs.getString("cd"));
                v_row.setName(v_rs.getString("name"));
                v_result.add(v_row);
                return v_row;
            }
        });
        
        List<SqlParameter> paramList = new ArrayList<SqlParameter>();
        paramList.add(oTable);

        Map<String, Object> resultMap = jdbc.call(new CallableStatementCreator() {
            @Override
            public CallableStatement createCallableStatement(Connection connection) throws SQLException {
                CallableStatement callableStatement = connection.prepareCall(v_sql_callProc);
                return callableStatement;
            }
        }, paramList);

        /*
        jdbc.query(v_sql_callProc,  new RowMapper<SavedHeaders>(){
            @Override
            public SavedHeaders mapRow(ResultSet v_rs, int rowNum) throws SQLException {
                SavedHeaders v_row = SavedHeaders.create();
                v_row.setHeaderId(v_rs.getLong("header_id"));
                v_row.setCd(v_rs.getString("cd"));
                v_row.setName(v_rs.getString("name"));
                v_result.add(v_row);
                return v_row;
            }
        });
        */

        // Local Temp Table DROP
        jdbc.execute(v_sql_dropable);

        context.setResult(v_result);
        context.setCompleted();


    }


    @Transactional(rollbackFor = SQLException.class)
    @On(event = SaveEmpMultiProcContext.CDS_NAME)
    public void onSaveEmpMultiProc(SaveEmpMultiProcContext context) {

        String v_sql_commitOption = "SET TRANSACTION AUTOCOMMIT DDL OFF;";
        String v_sql_createTable = "CREATE local TEMPORARY column TABLE #LOCAL_TEMP_EMP ( USERID NVARCHAR(20), ACTION NVARCHAR(5), OCCUPATIONALLEVELS NVARCHAR(20), WORKSCHEDULECODE NVARCHAR(30), FGTSOPTANT NVARCHAR(6), COMMITMENTINDICATOR NVARCHAR(20), ENDDATE TIMESTAMP ) ";    
        
        String v_sql_dropable = "DROP TABLE #LOCAL_TEMP_EMP";
        String v_sql_insertTable = "INSERT INTO #LOCAL_TEMP_EMP VALUES (?, ?, ?, ?, ?, ?, ?)";
        String v_sql_callProc = "CALL EMP_SAVE_PROC(I_TABLE => #LOCAL_TEMP_EMP, O_TABLE => ?)";

        Collection<EmpHeadersOutput> v_result = new ArrayList<>();
        Collection<EmpHeaders> v_inHeaders = context.getSampleEmployees();

        // Commit Option
        jdbc.execute(v_sql_commitOption);

        // Create Local Temp Table 
        jdbc.execute(v_sql_createTable);
    
        // Insert Local Temp Table
        List<Object[]> batch = new ArrayList<Object[]>();
        if(!v_inHeaders.isEmpty() && v_inHeaders.size() > 0){
            for(EmpHeaders v_inRow : v_inHeaders){
                Object[] values = new Object[] {
                    v_inRow.get("userId"),
                    v_inRow.get("action"),
                    v_inRow.get("occupationalLevels"),
                    v_inRow.get("workscheduleCode"),
                    v_inRow.get("fgtsOptant"),
                    v_inRow.get("commitmentIndicator"),
                    v_inRow.get("endDate") };

                batch.add(values);
            }
        }

        int[] updateCounts = jdbc.batchUpdate(v_sql_insertTable, batch);
       
        // Procedure Call
        SqlReturnResultSet oTable = new SqlReturnResultSet("O_TABLE", new RowMapper<EmpHeadersOutput>(){
            @Override
            public EmpHeadersOutput mapRow(ResultSet v_rs, int rowNum) throws SQLException {
                EmpHeadersOutput v_row = EmpHeadersOutput.create();
                v_row.setUserId(v_rs.getString("userId"));
                v_row.setUserIdSeq(v_rs.getLong("userId_seq"));
                v_row.setAction(v_rs.getString("action"));
                v_row.setOccupationalLevels(v_rs.getString("occupationalLevels"));
                v_row.setWorkscheduleCode(v_rs.getString("workscheduleCode"));
                v_row.setFgtsOptant(v_rs.getString("fgtsOptant"));
                v_row.setCommitmentIndicator(v_rs.getString("commitmentIndicator"));
                v_result.add(v_row);
                return v_row;
            }
        });
        
        List<SqlParameter> paramList = new ArrayList<SqlParameter>();
        paramList.add(oTable);

        Map<String, Object> resultMap = jdbc.call(new CallableStatementCreator() {
            @Override
            public CallableStatement createCallableStatement(Connection connection) throws SQLException {
                CallableStatement callableStatement = connection.prepareCall(v_sql_callProc);
                return callableStatement;
            }
        }, paramList);


        // Local Temp Table DROP
        jdbc.execute(v_sql_dropable);

        context.setResult(v_result);
        context.setCompleted();


    }    


    @Transactional(rollbackFor = SQLException.class)
    @On(event = SaveEmpJobMultiProcContext.CDS_NAME)
    public void onSaveEmpJobMultiProc(SaveEmpJobMultiProcContext context) {

        String v_sql_commitOption = "SET TRANSACTION AUTOCOMMIT DDL OFF;";
        String v_sql_createTable = "CREATE local TEMPORARY column TABLE #LOCAL_TEMP_EMPJOB ( USERID NVARCHAR(20), STARTDATE NVARCHAR(25), SEQNUMBER NVARCHAR(2),  RECORD NCLOB ) ";    
        
        String v_sql_dropable = "DROP TABLE #LOCAL_TEMP_EMPJOB";
        String v_sql_insertTable = "INSERT INTO #LOCAL_TEMP_EMPJOB VALUES (?, ?, ?, ?)";
        String v_sql_callProc = "CALL EMPJOB_SAVE_PROC(I_TABLE => #LOCAL_TEMP_EMPJOB, O_TABLE => ?)";

        Collection<EmpJobHeadersOutput> v_result = new ArrayList<>();
        Collection<EmpJobHeaders> v_inHeaders = context.getEmpJob();

        // Commit Option
        jdbc.execute(v_sql_commitOption);

        // Create Local Temp Table 
        jdbc.execute(v_sql_createTable);
    
        // Insert Local Temp Table
        List<Object[]> batch = new ArrayList<Object[]>();
        if(!v_inHeaders.isEmpty() && v_inHeaders.size() > 0){
            for(EmpJobHeaders v_inRow : v_inHeaders){
                Object[] values = new Object[] {
                    v_inRow.get("userId"),
                    v_inRow.get("startDate"),
                    v_inRow.get("seqNumber"),
                    v_inRow.get("Record")};
                batch.add(values);
            }
        }

        int[] updateCounts = jdbc.batchUpdate(v_sql_insertTable, batch);
       
        // Procedure Call
        SqlReturnResultSet oTable = new SqlReturnResultSet("O_TABLE", new RowMapper<EmpJobHeadersOutput>(){
            @Override
            public EmpJobHeadersOutput mapRow(ResultSet v_rs, int rowNum) throws SQLException {
                EmpJobHeadersOutput v_row = EmpJobHeadersOutput.create();
                v_row.setUserId(v_rs.getString("userId"));
                v_row.setStartDate(v_rs.getString("startDate"));
                v_row.setSeqNumber(v_rs.getString("seqNumber"));
                v_row.setUniquekey(v_rs.getLong("uniquekey"));
                v_result.add(v_row);
                return v_row;
            }
        });
        
        List<SqlParameter> paramList = new ArrayList<SqlParameter>();
        paramList.add(oTable);

        Map<String, Object> resultMap = jdbc.call(new CallableStatementCreator() {
            @Override
            public CallableStatement createCallableStatement(Connection connection) throws SQLException {
                CallableStatement callableStatement = connection.prepareCall(v_sql_callProc);
                return callableStatement;
            }
        }, paramList);


        // Local Temp Table DROP
        jdbc.execute(v_sql_dropable);

        context.setResult(v_result);
        context.setCompleted();


    }    
}
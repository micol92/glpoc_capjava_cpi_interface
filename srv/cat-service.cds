using {Sample_Header as Header} from '../db/schema';
using {Sample_Detail as Detail} from '../db/schema';
using {Sample_Employee as Employee} from '../db/schema';
using {EmpJob as EmpJobData} from '../db/schema';

@path : '/SampleMgrV4Service'
service SampleMgrV4Service {
   
        
    entity SampleHeaders as projection on Header;
    entity SampleDetails as projection on Detail;
    entity SampleEmployee as projection on Employee;
    entity EmpJob as projection on EmpJobData;



    type EmpJobHeaders : {
        userId : String(20);
        startDate : String(25);
        seqNumber: String(2);
        Record: LargeString;
    }
    
    type EmpJobHeadersOutput: {
        userId : String(20);
        startDate : String(25);
        seqNumber: String(2);
        uniquekey: Integer64;
    }
  
    type EmpHeaders : {
        userId : String(20);
        action : String(5);
        occupationalLevels: String(20);
        workscheduleCode: String(30);
        fgtsOptant: String(6);
        commitmentIndicator: String(20);
        endDate: Timestamp;
    }
    
    type EmpHeadersOutput: {
        userId : String(20);
        userId_seq : Integer64;
        action : String(5);
        occupationalLevels: String(20);
        workscheduleCode: String(30);
        fgtsOptant: String(6);
        commitmentIndicator: String(20);
        endDate: Timestamp;
    }

    type SavedHeaders : {
        header_id : Integer64;
        cd : String;
        name: String;
    };

    type SavedDetails : {
        detail_id : Integer64;
        header_id : Integer64;
        cd : String;
        name: String;
    };

    type SavedHeadersOutput : {
        header_id : Integer64;
        header_seq : Integer64;       
        cd : String;
        name: String;
    };

    // Header Multi Row
    /*********************************
    {
        "sampleHeaders" : [
            {"header_id" : 106, "cd": "eeee11", "name": "eeee11"},
            {"header_id" : 107, "cd": "eeee12", "name": "eeee12"}
        ]
    }
    *********************************/
    action SaveSampleHeaderMultiProc (sampleHeaders : array of SavedHeaders) returns array of SavedHeadersOutput;

    action SaveEmpMultiProc (sampleEmployees : array of EmpHeaders) returns array of EmpHeadersOutput;

    action SaveEmpJobMultiProc (EmpJob : array of EmpJobHeaders) returns array of EmpJobHeadersOutput;

    // Procedure Call header/Detail 
    // Header, Detail  multi
    // Test 
    /*********************************
    {
        "inputData" : {
            "savedHeaders" : [
                {"header_id" : 108, "cd": "eeee1122222", "name": "eeee11"},
                {"header_id" : 109, "cd": "eeee1222222", "name": "eeee12"}
            ],
            "savedDetails" : [
                {"detail_id": 1008, "header_id" : 108, "cd": "eeee122221", "name": "eeee11"},
                {"detail_id": 1009, "header_id" : 108, "cd": "eeee122222", "name": "eeee12"},
                {"detail_id": 1010, "header_id" : 109, "cd": "eeee122221", "name": "eeee11"},
                {"detail_id": 1011, "header_id" : 109, "cd": "eeee122222", "name": "eeee12"}
            ]
        }
    }
    *********************************/
    type saveReturnType {
        savedHeaders : array of SavedHeaders;
        savedDetails : array of SavedDetails;
    }
    

    action SaveSampleMultiEnitylProc (inputData : saveReturnType) returns saveReturnType;
  

    // (Single Header - multi Detail) 
    // Test data
    /*********************************
    {
        "inputData": [
            {
                "header_id" : 103,
                "cd" : "CD103",
                "name": "NAME103",
                "details": [
                    {"detail_id" : 1003, "header_id" : 103, "cd" : "CD1003", "name": "NAME1003"},
                    {"detail_id" : 1004, "header_id" : 103, "cd" : "CD1004", "name": "NAME1004"}
                ]
            },
            {
                "header_id" : 104,
                "cd" : "CD104",
                "name": "NAME104",
                "details": [
                    {"detail_id" : 1005, "header_id" : 104, "cd" : "CD1003", "name": "NAME1005"},
                    {"detail_id" : 1006, "header_id" : 104, "cd" : "CD1004", "name": "NAME1006"}
                ]
            }
        ]
    }
    *********************************/
    type hdSaveType {
        header_id : Integer64;
        cd : String;
        name: String;
        details:  array of SavedDetails;
    }
   
    action SaveSampleHeaderDetailProc (inputData : array of hdSaveType) returns array of hdSaveType;


    //parameter is must view
    /*
    view SampleViewCondition (header_cd: String, detail_cd: String) as 
    select h.header_id
          ,h.cd as header_cd
          ,h.name as header_name
          ,key d.detail_id
          ,d.cd as detail_cd
          ,d.name as detail_name
    from Header as h 
    left join Detail as d on h.header_id = d.header_id
    where h.cd = :header_cd
    and   d.cd = :detail_cd
    ;

    view SampleViewCondition2 as 
    select h.header_id
          ,h.cd as header_cd
          ,h.name as header_name
          ,key d.detail_id
          ,d.cd as detail_cd
          ,d.name as detail_name
    from Header as h 
    left join Detail as d on h.header_id = d.header_id
    ;
    */    
  //   entity MasterFunc(CD : String) as select from MasterF.Sample_Master_Func(CD: :CD);


}

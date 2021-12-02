
entity Sample_Header {
  key header_id : Integer64;
  header_seq : Integer64;
  cd : String;
  name: String;
  details: Association to many Sample_Detail on details.header_id = header_id;
  local_create_dtm: DateTime;
  local_update_dtm: DateTime;
  create_user_id: String @cds.on.insert: $user;
  update_user_id: String @cds.on.insert: $user @cds.on.update: $user;
  system_create_dtm: DateTime @cds.on.insert: $now;
  system_update_dtm: DateTime @cds.on.insert: $now  @cds.on.update: $now;
}


entity Sample_Detail {
  key detail_id : Integer64;
  header_id : Integer64;
  cd : String;
  name: String;
  local_create_dtm: DateTime;
  local_update_dtm: DateTime;
  create_user_id: String @cds.on.insert: $user;
  update_user_id: String @cds.on.insert: $user @cds.on.update: $user;
  system_create_dtm: DateTime @cds.on.insert: $now;
  system_update_dtm: DateTime @cds.on.insert: $now  @cds.on.update: $now;
}

entity Sample_Employee {
  key userId : String(20);
  key userId_seq : Integer64;
  action : String(5);
  occupationalLevels: String(20);
  workscheduleCode: String(30);
  fgtsOptant: String(6);
  commitmentIndicator: String(20);
  endDate: Timestamp;
  create_user_id: String @cds.on.insert: $user;
  update_user_id: String @cds.on.insert: $user @cds.on.update: $user;
  system_create_dtm: DateTime @cds.on.insert: $now;
  system_update_dtm: DateTime @cds.on.insert: $now  @cds.on.update: $now;
}

entity EmpJob {
  key userId : String(20);
  key startDate : String(25);
  key seqNumber : String(2);
  key uniquekey : Integer64;
  Record : LargeString;
  LargeRecord : LargeString;
  create_user_id: String @cds.on.insert: $user;
  update_user_id: String @cds.on.insert: $user @cds.on.update: $user;
  system_create_dtm: DateTime @cds.on.insert: $now;
  system_update_dtm: DateTime @cds.on.insert: $now  @cds.on.update: $now;
}

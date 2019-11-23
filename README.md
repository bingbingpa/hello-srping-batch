## postgres spring batch 테스트

- batch 관련 메타 테이블이 없으면 오류 발생하므로 배치를 실행하기전에 DB에 batch관련 메타 테이블이 있어야 한다. h2 같은 경우는 자동 생성 되지만 나머지 DB는 직접 생성해야 한다. 
- **BATCH_JOB_INSTANCE** : Job Paramter에 따라 Batch 애플리케이션이 정상적으로 수행되면 인스턴스 정보를 기록하는 테이블.
- 같은 파라미터로는 Job을 실행 시킬 수 없다.
- **BATCH_JOB_EXECUTION** : Batch 애플리케이션이 실행되었을 때, Job이 정상적으로 수행되었는지, 실패되었는지에 대한 정보를 기록하는 테이블. 
- **BATCH_JOB_EXECUTION_PARAMS** :  Batch가 실행될 때 넘긴 Job Paramter 정보를 기록하는 테이블. 
- 조건별 흐름 제어를 위해서는 **Decide**를 사용.
- JobParameters를 사용하기 위해선 꼭 **@StepScope, @JobScope로 Bean을 생성**해야한다.
- **@JobScope**는 Step 선언문에서 사용 가능하고, **@StepScope**는 Tasklet이나 ItemReader, ItemWriter, ItemProcessor에서 사용 가능.
- Job Parameter의 타입으로 사용할 수 있는 것은 Double, Long, Date, String
- Reader와 Processor에서는 1건씩 다뤄지고, Writer에선 Chunk 단위로 처리 된다.
- Chunk Size는 한번에 처리될 트랜잭션 단위를 얘기하며, Page Size는 한번에 조회할 Item의 양을 얘기한다. 즉 둘은 서로 의미하는 바가 다르다.
- Chunk Size와 Page Size의 값은 일치시키는게 보편적으로 좋은 방법이다. 
- Srping Batch는 Cursor, Paging 2개의 Reader 타입을 지원한다. Cursor는 하나의 Connection으로 Batch가 끝날때까지 사용되기 때문에 Batch가 끝나기전에 Database와 어플리케이션의 Connection이 먼저 끊어질수도 있다. 그래서 Batch 수행 시간이 오래 걸리는 경우에는 PagingItemReader를 사용하는게 낫다.
- PagingItemReader를 사용할 때는 무조건 Order를 포함해야 한다. 
- wirter에는 setAssertUpdates(false)를 주면 transaction관련 오류를 해결 할 수 있다.
- Writer에서 계속 무한루프가 돈다면 ItemReader에서 select하는 쿼리에 페이징 처리를 해야한다. 페이징이 없을 경우 같은 데이터를 계속해서 wirte한다.

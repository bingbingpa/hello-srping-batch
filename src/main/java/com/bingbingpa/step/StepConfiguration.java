// package com.bingbingpa.step;


// import java.util.HashMap;
// import java.util.Map;

// import javax.sql.DataSource;

// import org.apache.ibatis.session.SqlSessionFactory;
// import org.mybatis.spring.batch.MyBatisBatchItemWriter;
// import org.springframework.batch.core.Step;
// import org.springframework.batch.core.configuration.annotation.JobScope;
// import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
// import org.springframework.batch.core.configuration.annotation.StepScope;
// import org.springframework.batch.item.ItemProcessor;
// import org.springframework.batch.item.ItemWriter;
// import org.springframework.batch.item.database.JdbcPagingItemReader;
// import org.springframework.batch.item.database.Order;
// import org.springframework.batch.item.database.PagingQueryProvider;
// import org.springframework.batch.item.database.builder.JdbcPagingItemReaderBuilder;
// import org.springframework.batch.item.database.support.SqlPagingQueryProviderFactoryBean;
// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.beans.factory.annotation.Qualifier;
// import org.springframework.beans.factory.annotation.Value;
// import org.springframework.context.annotation.Bean;
// import org.springframework.context.annotation.Configuration;
// import org.springframework.jdbc.core.BeanPropertyRowMapper;

// import lombok.RequiredArgsConstructor;
// import lombok.extern.slf4j.Slf4j;
// import mnd.mgps.config.AltibaseQueryProvider;
// import mnd.mgps.domain.ProductDto;

// @Slf4j
// @RequiredArgsConstructor
// @Configuration
// public class StepConfiguration {

// 	private final StepBuilderFactory stepBuilderFactory;
// 	private final Integer CHUNK_SIZE = 10000;

// 	@Autowired 
// 	@Qualifier("altibaseDataSource") 
// 	private DataSource dataSource;

// 	@Autowired
// 	@Qualifier("altibaseSqlSessionFactory")
// 	public SqlSessionFactory altibaseSqlSessionFactory;
	
// 	@Autowired
// 	@Qualifier("postgresqlSqlSessionFactory")
// 	private SqlSessionFactory postgresqlSqlSessionFactory;

// 	@Bean(name = "productStep")
// 	@JobScope
// 	public Step productStep(@Value("#{jobParameters[version]}") String version) throws Exception{
// 		log.info("step ----------------------------- product");
// 		return stepBuilderFactory.get("productStep")
// 				.<ProductDto, ProductDto>chunk(CHUNK_SIZE)
// 				.reader(productReader(null))
// 				.processor(syncAltibaseProcessor())
// 				.writer(productWriter())
// 				.build();
// 	}

// 	@Bean
// 	@JobScope
//     public JdbcPagingItemReader<ProductDto> productReader(@Value("#{jobParameters[version]}") String version) throws Exception {
//         Map<String, Object> parameterValues = new HashMap<>();
//         parameterValues.put("version", version);

//         return new JdbcPagingItemReaderBuilder<ProductDto>()
//                 .pageSize(CHUNK_SIZE)
//                 .fetchSize(CHUNK_SIZE)
//                 .dataSource(dataSource)
//                 .rowMapper(new BeanPropertyRowMapper<>(ProductDto.class))
//                 .queryProvider(createQueryProvider())
//                 .parameterValues(parameterValues)
//                 .name("jdbcProductItemReader")
//                 .build();
// 	}
	
// 	@Bean
//     public PagingQueryProvider createQueryProvider() throws Exception {
// 		// SqlPagingQueryProviderFactoryBean queryProvider = new SqlPagingQueryProviderFactoryBean();
// 		AltibaseQueryProvider queryProvider = new AltibaseQueryProvider();
// 		StringBuffer select = new StringBuffer();
// 		StringBuffer from = new StringBuffer();
// 		select.append("       small_ctgy_cd AS product_type,   ")
// 			.append("       TRIM(SUBSTR(server_file_path,POSITION(server_file_path,'\',-1,1)+1, LENGTH(server_file_path)), CONCAT('.',data_format)) AS product_name,   ")
// 			.append("       CASE SUBSTR(small_ctgy_cd,0,2)   ")
// 			.append("           WHEN '06' THEN c.photoshot_date   ")
// 			.append("           WHEN '02' THEN d.print_date   ")
// 			.append("           END AS product_date,   ")     
// 			.append("       data_format AS file_ext,   ")
// 			.append("       CAST(data_size AS float) AS file_size,   ")
// 			.append("       server_overview_path AS thumbnail_path,   ")            
// 			.append("       server_file_path AS file_path,   ")
// 			.append("       asText(b.data_bndry_polygon) AS bbox,    ")
// 			.append("       asText(b.data_bndry_polygon) AS footprint,   ")
// 			.append("       a.data_id AS ref_data_id   ");
// 		from.append("   FROM spatial_info a   ") 
// 			.append("   LEFT JOIN scope_info b   ")
// 			.append("   on a.data_id = b.data_id   ")
// 			.append("   LEFT JOIN satellite_img_info c    ")
// 			.append("   on a.data_id = c.data_id   ")
// 			.append("   LEFT JOIN tlm_info d   ")
// 			.append("   on a.data_id = d.data_id   ");
//         // queryProvider.setDataSource(dataSource); // Database에 맞는 PagingQueryProvider를 선택하기 위해 
//         queryProvider.setSelectClause(select.toString());
//         queryProvider.setFromClause(from.toString());
//         queryProvider.setWhereClause("WHERE TO_CHAR(a.svr_reg_date, 'YYYYMMDD') >= :version");
// 		// queryProvider.setDatabaseType("Oracle");
//         Map<String, Order> sortKeys = new HashMap<>(1);
//         sortKeys.put("a.data_id", Order.ASCENDING);

// 		queryProvider.setSortKeys(sortKeys);

//         return queryProvider;
//     }

// 	// @Bean
// 	// @StepScope
//     // public JdbcCursorItemReader<ProductDto> productReader(@Value("#{jobParameters[version]}") String version) {

// 	// 	StringBuffer sql = new StringBuffer();
//     //         sql.append("   SELECT   ")
//     //             .append("       small_ctgy_cd AS product_type,   ")
//     //             .append("       TRIM(SUBSTR(server_file_path,POSITION(server_file_path,'\',-1,1)+1, LENGTH(server_file_path)), CONCAT('.',data_format)) AS product_name,   ")
//     //             .append("       CASE SUBSTR(small_ctgy_cd,0,2)   ")
//     //             .append("           WHEN '06' THEN c.photoshot_date   ")
//     //             .append("           WHEN '02' THEN d.print_date   ")
//     //             .append("           END AS product_date,   ")     
//     //             .append("       data_format AS file_ext,   ")
//     //             .append("       CAST(data_size AS float) AS file_size,   ")
//     //             .append("       server_overview_path AS thumbnail_path,   ")            
//     //             .append("       server_file_path AS file_path,   ")
//     //             .append("       asText(b.data_bndry_polygon) AS bbox,    ")
//     //             .append("       asText(b.data_bndry_polygon) AS footprint,   ")
//     //             .append("       a.data_id AS ref_data_id   ")
//     //             .append("   FROM spatial_info a   ") 
//     //             .append("   LEFT JOIN scope_info b   ")
//     //             .append("   on a.data_id = b.data_id   ")
//     //             .append("   LEFT JOIN satellite_img_info c    ")
//     //             .append("   on a.data_id = c.data_id   ")
//     //             .append("   LEFT JOIN tlm_info d   ")
//     //             .append("   on a.data_id = d.data_id   ");
// 	// 			if(version != null) {
// 	// 				sql.append("   WHERE TO_CHAR(a.svr_reg_date, 'YYYYMMDD') >="+ version);
// 	// 			}
//     //     return new JdbcCursorItemReaderBuilder<ProductDto>()
//     //             .dataSource(dataSource)
//     //             .rowMapper(new BeanPropertyRowMapper<>(ProductDto.class))
//     //             .sql(sql.toString())
//     //             .name("jdbcProductItemReader")
//     //             .build();
//     // }

// 	// @Bean
// 	// @StepScope
// 	// public ItemReader<ProductDto> productReader(@Value("#{jobParameters[version]}") String version) {
// 	// 	Map<String, Object> parameterValues = new HashMap<>();
// 	// 	parameterValues.put("version", (String)version);

// 	// 	MyBatisPagingItemReader<ProductDto> reader = new MyBatisPagingItemReader<ProductDto>();
// 	// 	reader.setSqlSessionFactory(altibaseSqlSessionFactory);
// 	// 	reader.setParameterValues(parameterValues);
// 	// 	reader.setQueryId("mnd.mgps.persistence.altibase.DBSyncMapper.getListProduct");
		
//     //     return reader;
// 	// }
	
// 	@Bean
// 	@StepScope
//     public ItemProcessor<ProductDto, ProductDto> syncAltibaseProcessor() {
// 		return new ItemProcessor<ProductDto, ProductDto>() {
//            @Override
//            public ProductDto process(ProductDto i) throws Exception {
// 			ProductDto o = new ProductDto();
//                return o;
		  
// 			}
// 		};
// 	}
		
		  
//     @Bean
// 	@StepScope
//     public ItemWriter<ProductDto> productWriter() {
//         MyBatisBatchItemWriter<ProductDto> writer = new MyBatisBatchItemWriter<ProductDto>();
//         writer.setSqlSessionFactory(postgresqlSqlSessionFactory);
// 		writer.setStatementId("mnd.mgps.persistence.postgresql.DBSyncMapper.insertProduct");
// 		writer.setAssertUpdates(false);

//         return writer;
//     }
// }
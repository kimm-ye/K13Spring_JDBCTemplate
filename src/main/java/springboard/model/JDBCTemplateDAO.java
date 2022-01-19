package springboard.model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Map;

import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
/*
JDBCTemplate 관련 주요 메서드

-레코드 딱 하나만 반환, 카운트 같은 것을 반환하기 때문에 반환타입이 뭔지 모르니까 Object 타입
Object queryForObject(String sql, RowMapper rm)
	: 하나의 레코드나 결과값을 반환하는 select 계열의 쿼리문을 실행할때 사용한다. (RowMapper는 반환타입)
Object queryForObject(String sql, Object[] args, RowMapper rm)
	: 인파라미터가 있고, 하나의 레코드를 반환하는 select계열의 쿼리문 실행에 사용한다.

-여러개를 반환해야해서 항상 List컬렉션에 저장해야하기 때문에 반환타입이 List 
List query(String sql, RowMapper rm)
	: 여러개의 레코드를 반환하는 select계열의 쿼리문인 경우 사용한다.
List query(String sql, Object[] args, RowMapper rm)
	: 인파라미터를 가진 여러개의 레코드를 반환하는 select계열의 쿼리문인 경우 사용한다.

-처리되고 안되고니까 int형으로 반환한다. (되면 1, 안되면 0)
int update(String sql)
	: 인파라미터가 없는 update/insert/delete 쿼리문을 처리할 때 사용한다.
int update(String sql, Object[] args)
	: 인파라미터가 있는 update/insert/delete 쿼리문을 처리할 때 사용한다.
 */
import org.springframework.jdbc.core.PreparedStatementCreator;

public class JDBCTemplateDAO {

	//템플릿이라는 멤버변수
	JdbcTemplate template;

	//생성자 : 멤버변수 초기화하고 있다
	public JDBCTemplateDAO() {
		//static으로 만들었던 녀석을 그대로 가지고와서 초기화하고 있음.
		/*
		컨트롤러에서 @Autowired를 통해 자동 주입받았던 빈을 정적변수인
		JdbcTemplateConst.template에 값을 할당하였으므로, DB연결정보를
		DAO에서 바로 사용할 수 있다.
		*/
		this.template = JdbcTemplateConst.template;
		System.out.println("JDBCTemplateDAO() 생성자 호출");
	}
	public void close() {
		//JDBCTemplate에서는 사용하지 않음
		/*
		JDBCTemplate에서는 자원해제를 하지 않는다.
		Spring 설정파일에서 빈을 생성하므로 자원을 해제하면
		다시 new를 통해 생성해야 하므로 자원해제를 사용하지 않는다.
		*/
	}
	
	//게시물의 갯수 카운트
	public int getTotalCount(Map<String, Object> map) {
		
		String sql = " SELECT COUNT(*) FROM springboard ";
		
		if(map.get("Word")!=null) {
			sql+= " WHERE " + map.get("Column") + " Like '%" + map.get("Word") + "%' ";
		}
		//쿼리문에서 count(*)을 통해 반환되는 값을 정수형태로 가져온다.
		return template.queryForObject(sql, Integer.class);
	}
	
	//게시판 리스트 가져오기(페이지 처리 없음)
	public ArrayList<SpringBbsDTO> list(Map<String, Object> map){
		String sql = " SELECT * FROM springboard ";
		if(map.get("Word")!= null) {
			sql+= " WHERE " + map.get("Column") + " Like '%" + map.get("Word") + "%' ";
		}
		sql += " ORDER BY idx DESC ";
		
		/*
		RowMapper가 select를 통해 얻어온 ResultSet을 DTO객체에 저장하고,
		List컬렉션에 적재하여 반환한다. 
		그러므로 DAO에서 개발자가 반복적으로 하던 작업을 자동으로 처리해준다.
		 */
		return (ArrayList<SpringBbsDTO>) template.query(sql, new BeanPropertyRowMapper<SpringBbsDTO>(SpringBbsDTO.class));
	}
	
	
	//글쓰기 처리
	public int write(final SpringBbsDTO springBbsDTO) {
		
		//쓰기,수정,삭제 등은 updateQuery를 사용하는 것처럼 여기도 update사용
		//작성된 폼값을 저장한 DTO객체를 매개변수로 전달 받음
		/*
		매개변수로 전달된 값을 익명클래스 내에서 사용할때는
		반드시 final로 선언하여 값 변경이 불가능하게 처리해야 한다.
		final로 선언하지 않으면 에러가 발생한다. 이것은 Java의 규칙이다.
		 */
		int result = template.update(new PreparedStatementCreator() {
			
			@Override
			public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
				
				//쿼리설정
				/*
				하나의 쿼리문 내에서 nextval을 여러번 사용하더라도 항상 같은 
				시퀀스를 반환한다.
				 */
				String sql = " INSERT INTO springboard (idx, name, title, contents, hits "
						+ " , bgroup, bstep, bindent, pass) VALUES "
						+ " (springboard_seq.NEXTVAL, ?, ?, ?, 0, "
						+ " springboard_seq.NEXTVAL, 0, 0, ?) ";
				
				//쿼리에 넣을 인파라미터 설정
				PreparedStatement psmt = con.prepareStatement(sql);
				psmt.setString(1, springBbsDTO.getName());
				psmt.setString(2, springBbsDTO.getTitle());
				psmt.setString(3, springBbsDTO.getContents());
				psmt.setString(4, springBbsDTO.getPass());
				
				return psmt;
			}
		});
		
		return result;
	}
}



















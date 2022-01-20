package springboard.command;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.ui.Model;

import springboard.model.JDBCTemplateDAO;
import springboard.model.SpringBbsDTO;

public class ViewCommand implements BbsCommandImpl{
	
	@Override
	public void execute(Model model) {

		//요청 일괄 받기 (한꺼번에 받는거니까)
		Map<String, Object> paramMap = model.asMap();
		//꺼낼때는 Object타입이기 때문에 HttpServletRequest로 형변환
		HttpServletRequest req = (HttpServletRequest)paramMap.get("req");
		
		//폼값받기
		String idx = req.getParameter("idx");
		String nowPage = req.getParameter("nowPage");
		
		//해당 게시물 select하기 위해 dao, dto 객체 생성.
		//DAO, DTO객체 생성 및 상세보기 처리를 위한 메서드 호출
		JDBCTemplateDAO dao = new JDBCTemplateDAO();
		SpringBbsDTO dto = new SpringBbsDTO();
		//dto객체에 일련번호를 전달한다.
		dto = dao.view(idx);

		//줄바꿈처리를 위해 <br/>로 변경
		dto.setContents(dto.getContents().replace("\r\n", "<br/>"));
		
		model.addAttribute("viewRow",dto);
		model.addAttribute("nowPage", nowPage);
		
	}
}

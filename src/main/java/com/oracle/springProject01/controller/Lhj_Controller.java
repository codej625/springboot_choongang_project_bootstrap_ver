package com.oracle.springProject01.controller;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import javax.mail.internet.MimeMessage;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.FileCopyUtils;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.oracle.springProject01.model.AttachmentFileVO;
import com.oracle.springProject01.model.Lhj_MemberVO;
import com.oracle.springProject01.model.Lhj_OAuthToken;
import com.oracle.springProject01.model.Member;
import com.oracle.springProject01.service.lhjService.KakaoAPI;
import com.oracle.springProject01.service.lhjService.MemberService;
import com.oracle.springProject01.service.paging.Paging;
import com.oracle.springProject01.service.yjhService.PostService;

@Controller
public class Lhj_Controller {

	@Autowired
	private KakaoAPI kakao;

	@Autowired
	private MemberService ms;

	@Autowired
	private PostService ps;

	@Autowired
	private com.oracle.springProject01.service.ljwService.MemberService mms;

	@Autowired
	private JavaMailSender mailSender;

	// ??????

	private static final Logger logger = org.slf4j.LoggerFactory.getLogger(Lhj_Controller.class);

	// ????????????
//	@RequestMapping(value = "/main/main")
//	public String main() {
//		System.out.println("go main");
//		return "main/main";
//	}

	// ???????????? ??????
	@RequestMapping(value = "/member/join", method = RequestMethod.GET)
	public String insertGET() throws Exception {
		System.out.println("Lhj_Controller String insertGET start...");
		logger.info("C: ???????????? ??????????????? GET");
		return "member/join";
	}

	// ???????????? ??????
	@RequestMapping(value = "/members/join", method = RequestMethod.POST)
	public String insertPOST(Lhj_MemberVO lhj_MemberVO) throws Exception {
		System.out.println("Lhj_Controller String insertPOST start...");
		logger.info("C: " + lhj_MemberVO);
		ms.insertMember(lhj_MemberVO);
		logger.info("C: ???????????? ??????????????? POST");

		return "member/login";
	}

	// ????????? ???????????? ??????
	@RequestMapping(value = "/idOverlap", method = RequestMethod.POST)
	public void idOverlap(HttpServletResponse response, @RequestParam("m_id") String m_id) throws IOException {
		System.out.println("Lhj_Controller String idOverlap start...");
		ms.idOverlap(m_id, response); // ???????????? ?????? idOverlap ??????.
	}

	// ???????????? ???????????? ??????
	@RequestMapping(value = "/telOverlap", method = RequestMethod.POST)
	public void telOverlap(HttpServletResponse response, @RequestParam("m_tel") String m_tel) throws IOException {
		System.out.println("Lhj_Controller String telOverlap start...");
		ms.telOverlap(m_tel, response); // ???????????? ?????? idOverlap ??????.
	}

	// ???????????? ?????? for ?????????
	@RequestMapping(value = "/member/Naverjoin", method = RequestMethod.POST)
//	@ResponseBody
//	public String Naverjoin(String m_id)
	public String Naverjoin(HttpServletResponse response, Lhj_MemberVO lhj_MemberVO, @RequestParam("m_id") String m_id)
			throws Exception {
		System.out.println("Lhj_Controller String Naverjoin start..." + m_id);

		ms.insertMember_NaverID(lhj_MemberVO);

		return "member/join";
	}

	// ????????? ????????? api
	@RequestMapping(value = "/member/kakaoCallback")
	public String login(@RequestParam("code") String code, HttpSession session) {
		String access_Token = kakao.getAccessToken(code);
		System.out.println("controller access_token : " + access_Token);

		HashMap<String, Object> userInfo = kakao.getUserInfo(access_Token);
		System.out.println("login Controller : " + userInfo);

		String m_name = (String) userInfo.get("nickname");
		System.out.println("nickname" + m_name);
		String m_id = (String) userInfo.get("email");
		System.out.println("m_id" + m_id);

		session.setAttribute("m_id", m_id);
		session.setAttribute("m_name", m_name);

		return "/member/kakaoCallback";
	}

	// ???????????? ?????? for ?????????
	@RequestMapping(value = "/member/kakaojoin", method = RequestMethod.POST)
	public String kakaojoin(String m_id, String m_name, HttpSession session, HttpServletResponse response,
			Lhj_MemberVO lhj_MemberVO, HttpServletRequest request) {
		System.out.println("Lhj_Controller String kakaojoin start...");

		ms.insertMember_KAKAOID(lhj_MemberVO, m_name, m_id);

//			session.setAttribute("m_id", request.getSession("sessionID", m_id));
//			session.setAttribute("m_name", m_name);

//			ms.insertMember_KAKAOID(lhj_MemberVO, m_name, m_id);

		return "member/join";
	}

	// ????????? ??????
	@RequestMapping(value = "/member/login", method = RequestMethod.GET)
	public String loginGET() throws Exception {
		System.out.println("Lhj_Controller String loginGET start...");
		logger.info("C: ????????? ??????????????? GET");
		return "member/login";
	}

	// ????????? ??????
	@RequestMapping(value = "/member/login", method = RequestMethod.POST)
	public String loginPOST(Lhj_MemberVO lhj_MemberVO, String m_id, Model model, HttpServletRequest request)
			throws Exception {
		System.out.println("LoginController login Start...");
		Lhj_MemberVO login = ms.login(lhj_MemberVO);
		System.out.println("LoginController result->" + login);
		if (login == null) {
			logger.info("????????? ?????? ???????????? ??????");
			return "redirect:/member/login";
		} else {
			// ????????????
			request.getSession().setAttribute("sessionID", m_id);
			model.addAttribute("lhj_MemberVO", login);
			model.addAttribute("m_id", m_id);
			System.out.println("m_id->" + m_id);
			return "/main/main";
		}
	}

	// ????????? ?????????
	@RequestMapping(value = "/member/naverlogin")
	public String naverlogin(HttpSession session) {
		System.out.println("Lhj_Controller String naverlogin start...");

		return "member/naverlogin";
	}

	// ????????? ????????? callback
	@RequestMapping(value = "/member/naverCallback")
	public String naverLogin22(HttpSession session) {
		System.out.println("Lhj_Controller String naverLogin22 start...");

		return "member/naverCallback";
	}

	// ????????? ????????? ??????
	@RequestMapping(value = "/personalInfo")
	public void personalInfo(HttpServletRequest request) throws Exception {
		String token = "AAAAOQIMd4SfzNVvGG92FuilHjnWC7BKydahyKXrdSTry31QSzxKH5BNpDV55fd5Y_xqtxUOAA8fLRVD3e60pM2-y-Y";// ?????????
																														// ?????????
																														// ??????
																														// ??????;
																														// ?????????
																														// ?????????
																														// ????????????
																														// ???????????????.
		String header = "Bearer " + token; // Bearer ????????? ?????? ??????
		try {
			String apiURL = "https://openapi.naver.com/v1/nid/me";
			URL url = new URL(apiURL);
			HttpURLConnection con = (HttpURLConnection) url.openConnection();
			con.setRequestMethod("GET");
			con.setRequestProperty("Authorization", header);
			int responseCode = con.getResponseCode();
			BufferedReader br;
			if (responseCode == 200) { // ?????? ??????
				br = new BufferedReader(new InputStreamReader(con.getInputStream()));
			} else { // ?????? ??????
				br = new BufferedReader(new InputStreamReader(con.getErrorStream()));
			}
			String inputLine;
			StringBuffer response = new StringBuffer();
			while ((inputLine = br.readLine()) != null) {
				response.append(inputLine);
			}
			br.close();
			System.out.println(response.toString());
		} catch (Exception e) {
			System.out.println(e);
		}
	}

	// ????????? ????????? ????????? ??????
	@RequestMapping(value = "/member/find_m_id")
	public String find_m_id(Lhj_MemberVO lhj_MemberVO, Model model) {
		System.out.println("Lhj_Controller String find_m_id start...");
		return "member/find_m_id";
	}

	// ????????? ????????? ?????? ??????
	@RequestMapping(value = "/member/find_m_id", method = RequestMethod.POST)
	public String find_idAction(Lhj_MemberVO lhj_MemberVO, Model model) {
		System.out.println("Lhj_Controller String find_idAction start...");

		Lhj_MemberVO lhj_MemberVO2 = ms.find_m_id(lhj_MemberVO);
		if (lhj_MemberVO2 == null) {
			model.addAttribute("check", 1);
		} else {
			model.addAttribute("check", 0);
			model.addAttribute("m_id", lhj_MemberVO2.getM_id());
		}
		return "member/find_m_id";
	}

	// ????????? ???????????? ????????? ??????
	@RequestMapping(value = "/member/find_m_pw")
	public String find_m_pw(Lhj_MemberVO lhj_MemberVO, Model model) {
		System.out.println("Lhj_Controller String find_m_pw start...");
		return "member/find_m_pw";
	}

	// ????????? ???????????? ?????? ??????
	@RequestMapping(value = "/member/find_m_pw", method = RequestMethod.POST)
	public String find_pwAction(Lhj_MemberVO lhj_MemberVO, Model model) {
		System.out.println("Lhj_Controller String find_pwAction start...");

		Lhj_MemberVO lhj_MemberVO2 = ms.find_m_pw(lhj_MemberVO);
		if (lhj_MemberVO2 == null) {
			model.addAttribute(lhj_MemberVO);
			model.addAttribute("check", 1);
		} else {
			model.addAttribute(lhj_MemberVO);
			model.addAttribute("check", 0);
			model.addAttribute("m_pw", lhj_MemberVO2.getM_pw());
		}
		return "member/find_m_pw";
	}

	// ???????????? ??????
	@RequestMapping(value = "/member/logout")
	public String logout(HttpSession session) {
		System.out.println("LoginController logout Start...");
		session.setAttribute("sessionID", null);
		session.invalidate();
		return "/main/main";
	}
	
	// ???????????????
	@RequestMapping(value = "/member/mypage")
	public String mypage(Model model, HttpServletRequest request, String m_id) throws Exception {
		System.out.println("lhjController mypage Start...");
		String sessionID = (String) request.getSession().getAttribute("sessionID");
		m_id = sessionID;
		Lhj_MemberVO lhj_MemberVO = ms.selectMypage(m_id);
		System.out.println("????????? ??? ??????????????? ??????" + m_id);
		model.addAttribute("lhj_MemberVO", lhj_MemberVO);

		return "/member/mypage";
	}

	// ??????????????? ?????? ???
	@RequestMapping(value = "/member/mypage_update", method = RequestMethod.GET)
	public String mypage_update(Model model, HttpServletRequest request, String m_id) throws Exception {
		System.out.println("lhjController mypage_update Start...");
		String sessionID = (String) request.getSession().getAttribute("sessionID");
		m_id = sessionID;
		Lhj_MemberVO lhj_MemberVO = ms.selectMypage(m_id);
		model.addAttribute("lhj_MemberVO", lhj_MemberVO);

		return "/member/mypage_update";
	}

	// ??????????????? ?????? ??????
	@RequestMapping(value = "/member/mypage_update", method = RequestMethod.POST)
	public String mypage_update(Model model, HttpServletRequest request, String m_id, HttpSession session,
			MultipartFile m_img) throws IOException, Exception {
		System.out.println("lhjController mypage_update222 Start...");
		String sessionID = (String) request.getSession().getAttribute("sessionID");
		m_id = sessionID;
		Lhj_MemberVO lhj_MemberVO = new Lhj_MemberVO();
		lhj_MemberVO.setM_name(request.getParameter("m_name"));
		lhj_MemberVO.setM_tel(request.getParameter("m_tel"));
		lhj_MemberVO.setM_area(request.getParameter("m_area"));
		lhj_MemberVO.setM_field(request.getParameter("m_field"));
		lhj_MemberVO.setM_id(request.getParameter("m_id"));

		String uploadPath = request.getSession().getServletContext().getRealPath("/upload/");
		String savedName = uploadFile(m_img.getOriginalFilename(), m_img.getBytes(), uploadPath);
		System.out.println("uploadPath: " + uploadPath);
		System.out.println("savedName: " + savedName);
		lhj_MemberVO.setM_img(savedName);
		lhj_MemberVO = ms.mypageUpdate(lhj_MemberVO);
		model.addAttribute("lhj_MemberVO", lhj_MemberVO);

		return "/member/mypage_update";
	}

	// ?????? ????????? ??????
	private String uploadFile(String originalName, byte[] fileData, String uploadPath) throws Exception {
		UUID uid = UUID.randomUUID();
		// requestPath = requestPath + "/resources/image";
		System.out.println("uploadPath->" + uploadPath);
		// Directory ??????
		File fileDirectory = new File(uploadPath);
		if (!fileDirectory.exists()) {
			fileDirectory.mkdirs();
			System.out.println("???????????? ?????? ?????? : " + uploadPath);
		}

		String savedName = uid.toString() + "_" + originalName;
		logger.info("savedName: " + savedName);
		File target = new File(uploadPath, savedName);
//	    File target = new File(requestPath, savedName);
		FileCopyUtils.copy(fileData, target); // org.springframework.util.FileCopyUtils

		return savedName;
	}

	// ??????????????? ???????????? ?????? ??????
	@RequestMapping(value = "/member/mypage_changePW", method = RequestMethod.GET)
	public String mypage_changePW(Model model, HttpServletRequest request, String m_id) throws Exception {
		System.out.println("lhjController mypage_changePW Start...");
		String sessionID = (String) request.getSession().getAttribute("sessionID");
		m_id = sessionID;
		Lhj_MemberVO lhj_MemberVO = ms.selectMypage(m_id);
		model.addAttribute(sessionID);
		model.addAttribute("lhj_MemberVO", lhj_MemberVO);

		return "/member/mypage_changePW";
	}

	// ???????????? ?????? ??????
	@RequestMapping(value = "/member/mypage_changePW", method = RequestMethod.POST)
	public String mypage_changePW(Lhj_MemberVO lhj_MemberVO, Model model, HttpServletRequest request, String m_id,
			HttpSession session) {
		System.out.println("lhjController mypage_changePW22 Start...");
		String sessionID = (String) request.getSession().getAttribute("sessionID");
		m_id = sessionID;
		lhj_MemberVO = ms.myPWchange(lhj_MemberVO);
		System.out.println("lhj_MemberVO->" + lhj_MemberVO.getM_id());
		model.addAttribute(sessionID);
		model.addAttribute("lhj_MemberVO", lhj_MemberVO);

		return "/member/mypage_changePW";
	}

	// ??????????????? ?????? ?????? //?????? api ?????? ??????
	@RequestMapping(value = "/member/mypage_myreginfo")
	public String myreginfo(Model model, HttpServletRequest request, String m_id) throws Exception {
		System.out.println("lhjController myreginfo Start...");
		String sessionID = (String) request.getSession().getAttribute("sessionID");
		m_id = sessionID;
		Lhj_MemberVO lhj_MemberVO = ms.selectMypage(m_id);
		// ?????? ?????????
		List<Lhj_MemberVO> myRegInfoList = ms.myRegInfoList(m_id);
		// ????????? ?????????
		List<Lhj_MemberVO> myRegInfo_classList = ms.myRegInfo_classList(m_id);
		// ?????? ?????????
		List<Lhj_MemberVO> myRegInfo_meetingList = ms.myRegInfo_meetingList(m_id);

		model.addAttribute("lhj_MemberVO", lhj_MemberVO);
		model.addAttribute("myRegInfoList", myRegInfoList);
		model.addAttribute("myRegInfo_classList", myRegInfo_classList);
		model.addAttribute("myRegInfo_meetingList", myRegInfo_meetingList);

		return "/member/mypage_myreginfo";
	}

	// ??????????????? ???????????? post?????? ???????????????

	// ??????????????? ???????????? ??????
	@RequestMapping(value = "/member/mypage_myreginfoDE", method = { RequestMethod.GET, RequestMethod.POST })
	public String myreginfoDE(Lhj_MemberVO lhj_MemberVO, Model model, HttpServletRequest request, HttpSession session)
			throws Exception {
		System.out.println("lhjController myreginfo Start...");
		String sessionID = (String) request.getSession().getAttribute("sessionID");
		String m_id = sessionID;
		lhj_MemberVO = ms.myRGNO(lhj_MemberVO);
		model.addAttribute("lhj_MemberVO", lhj_MemberVO);

		return "forward:/member/mypage_myreginfo";
	}

	// ??????????????? ?????? ??????
	@RequestMapping(value = "/member/mypage_mybookmark")
	public String mybookmark(Model model, HttpServletRequest request, String m_id) throws Exception {
		System.out.println("lhjController mybookmark Start...");
		String sessionID = (String) request.getSession().getAttribute("sessionID");
		m_id = sessionID;
		Lhj_MemberVO lhj_MemberVO = ms.selectMypage(m_id);
		// ?????? ????????? ?????????
		List<Lhj_MemberVO> myBookMarkList = ms.myBookMarkList(m_id);
		// ????????? ????????? ?????????
		List<Lhj_MemberVO> myBookMark_classList = ms.myBookMark_classList(m_id);
		// ????????? ?????? ?????????
		List<Lhj_MemberVO> myBookMark_meetingList = ms.myBookMark_meetingList(m_id);
		model.addAttribute("myBookMarkList", myBookMarkList);
		model.addAttribute("myBookMark_classList", myBookMark_classList);
		model.addAttribute("myBookMark_meetingList", myBookMark_meetingList);
		model.addAttribute("lhj_MemberVO", lhj_MemberVO);

		return "/member/mypage_mybookmark";
	}

	// ??????????????? ???????????? ??????
	@RequestMapping(value = "/member/mypage_mybookmarkSin", method = { RequestMethod.GET, RequestMethod.POST })
	public String mybookmarkSin(Lhj_MemberVO lhj_MemberVO, Model model, HttpServletRequest request,
			HttpSession session) {
		System.out.println("lhjController mybookmarkSin Start...");
		String sessionID = (String) request.getSession().getAttribute("sessionID");
		String m_id = sessionID;
		lhj_MemberVO = ms.myBMtoRG(lhj_MemberVO);
//		Lhj_MemberVO lhj_MemberVO2 = ms.myBMtoRG2(lhj_MemberVO);
		int bt_num = lhj_MemberVO.getBt_num();
		int bc_num = lhj_MemberVO.getBc_num();
		int p_num = lhj_MemberVO.getP_num();
		String p_cstatus = lhj_MemberVO.getP_cstatus();
		int post = ps.postRegInfoInsert(m_id, bt_num, bc_num, p_num, p_cstatus);
		System.out.println("lhjController mybookmarkSin int bt_num->" + bt_num);
		if (post > 0) {
			int postUpdate = ps.postCapaMinusUpdate(bt_num, bc_num, p_num);
		}
		model.addAttribute("lhj_MemberVO", lhj_MemberVO);
//		model.addAttribute("lhj_MemberVO", lhj_MemberVO2);

		return "forward:/member/mypage_myreginfo";
	}

	// ??????????????? ???????????? ??????
	@RequestMapping(value = "/member/mypage_mybookmarkNO", method = { RequestMethod.GET, RequestMethod.POST })
	public String mybookmarkNO(Lhj_MemberVO lhj_MemberVO, Model model, HttpServletRequest request,
			HttpSession session) {
		System.out.println("lhjController mybookmarkSin Start...");
		String sessionID = (String) request.getSession().getAttribute("sessionID");
		String m_id = sessionID;
		lhj_MemberVO = ms.myBMNO(lhj_MemberVO);
		model.addAttribute("lhj_MemberVO", lhj_MemberVO);

		return "forward:/member/mypage_mybookmark";
	}

	// ??????????????? ?????? ???
	@RequestMapping(value = "/member/mypage_deleteMyself")
	public String myDelMySelf(Lhj_MemberVO lhj_MemberVO, Model model, HttpServletRequest request, HttpSession session) {
		System.out.println("lhjController mybookmarkSin Start...");
		String sessionID = (String) request.getSession().getAttribute("sessionID");
		String m_id = sessionID;
		lhj_MemberVO = ms.selectMypage(m_id);
		System.out.println("????????? ??? ??????????????? ?????? for pw" + m_id);
		model.addAttribute("lhj_MemberVO", lhj_MemberVO);

		return "/member/mypage_deleteMyself";
	}

	// ??????????????? ??????
	@RequestMapping(value = "/member/mypage_deleteMyselfs", method = { RequestMethod.GET, RequestMethod.POST })
	public String myDelMySelf2(Lhj_MemberVO lhj_MemberVO, Model model, HttpServletRequest request,
			HttpSession session) {
		System.out.println("lhjController mybookmarkSin Start...");
		String sessionID = (String) request.getSession().getAttribute("sessionID");
		String m_id = sessionID;
		lhj_MemberVO = ms.myDelMySelf2(lhj_MemberVO);
		model.addAttribute("lhj_MemberVO", lhj_MemberVO);

		return "forward:/member/logout";
	}

	// ??????????????? ?????? ??????
	@RequestMapping(value = "/member/mypage_mycertification", method = RequestMethod.GET)
	public String mypage_mycertification(Model model, HttpServletRequest request, String m_id) throws Exception {
		System.out.println("lhjController mypage_changePW Start...");
		String sessionID = (String) request.getSession().getAttribute("sessionID");
		m_id = sessionID;
		Lhj_MemberVO lhj_MemberVO = ms.selectMypage(m_id);
		model.addAttribute("lhj_MemberVO", lhj_MemberVO);

		return "/member/mypage_mycertification";
	}

	// ??????????????? ?????? ??? ??? ??????
	@RequestMapping(value = "/member/mypage_myPostList", method = RequestMethod.GET)
	public String mypage_myPostList(String currentPage, Model model, HttpServletRequest request, String m_id)
			throws Exception {
		System.out.println("lhjController mypage_changePW Start...");
		String sessionID = (String) request.getSession().getAttribute("sessionID");
		m_id = sessionID;
		Lhj_MemberVO lhj_MemberVO = ms.selectMypage(m_id);
		// ?????? ??? ??? ????????? -all
		List<Lhj_MemberVO> myPostList = ms.myPostList(m_id);
		// ?????? ??? ??? ????????? -class
		List<Lhj_MemberVO> myPostList_class = ms.myPostList_class(m_id);
		// ?????? ??? ??? ????????? -meeting
		List<Lhj_MemberVO> myPostList_meeting = ms.myPostList_meeting(m_id);
		model.addAttribute("myPostList", myPostList);
		model.addAttribute("myPostList_class", myPostList_class);
		model.addAttribute("myPostList_meeting", myPostList_meeting);
		model.addAttribute("lhj_MemberVO", lhj_MemberVO);

		return "/member/mypage_myPostList";
	}

	// ??????????????? ?????? ????????? ?????? ?????? ??????
	@RequestMapping(value = "/member/mypage_myPostMEmberList_", method = RequestMethod.GET)
	public String mypage_myPostMEmberList(Lhj_MemberVO lhj_MemberVO, Model model, HttpServletRequest request,
			String m_id) throws Exception {
		System.out.println("lhjController mypage_changePW Start...");
		String sessionID = (String) request.getSession().getAttribute("sessionID");
		m_id = sessionID;
		// ?????? ??? ??? ????????? -all
		List<Lhj_MemberVO> mypage_myPostMEmberList = ms.mypage_myPostMEmberList(lhj_MemberVO);
		model.addAttribute("mypage_myPostMEmberList", mypage_myPostMEmberList);
		model.addAttribute("lhj_MemberVO", lhj_MemberVO);

		return "/member/mypage_myPostMEmberList_";
	}

	// ??????????????? ?????? ??????
	@RequestMapping(value = "/member/mypage_mycertification")
	public String mypage_mycertification(Model model, HttpServletRequest request, Lhj_MemberVO lhj_MemberVO,
			String m_id) throws Exception {
		System.out.println("lhjController mypage_mycertification Start...");
		String sessionID = (String) request.getSession().getAttribute("sessionID");
		m_id = sessionID;
		// m_id??? ?????? ???????????? ???????????? ?????? DTO????????? ??????
		lhj_MemberVO.setM_id(m_id);
		lhj_MemberVO = ms.mypage_mycertification(lhj_MemberVO);
		model.addAttribute("lhj_MemberVO", lhj_MemberVO);
		System.out.println("lhjController mypage_mycertification return...");
		return "/member/mypage_mycertification";
	}

	// ?????? ??????
	@RequestMapping(value = "/member/mailTransport", method = RequestMethod.POST)
	@ResponseBody
	public void mailTransport(Model model, HttpServletRequest request, Lhj_MemberVO lhj_MemberVO, String m_id) {
		System.out.println("lhjController mailTransport Start...");
		String sessionID = (String) request.getSession().getAttribute("sessionID");
		m_id = sessionID;
		// ?????? ?????? ?????????
		String tomail = lhj_MemberVO.getM_id();
		// ????????? m_id??? ?????????
		System.out.println(tomail);
		// ????????? ?????? ?????????
		String setfrom = "codej625s@gmail.com";
		String title = "???????????? ?????????.";
		try {
			MimeMessage message = mailSender.createMimeMessage();
			// "UTF-8"???????????? ???????????? ????????? ?????? ?????? ??????
			MimeMessageHelper messageHelper = new MimeMessageHelper(message, true, "UTF-8");
			// ????????? ?????? set
			messageHelper.setFrom(setfrom);
			// ?????? ?????? set
			messageHelper.setTo(tomail);
			// ?????? ?????? set
			messageHelper.setSubject(title);
			// Math?????? ????????? ?????? ???????????? ?????????
			String tempPassword = (int) (Math.random() * 999999) + 1 + "";
			// ?????? ????????? ???????????? ?????? ????????? DB??? ???????????? VO?????? setter??? ???????????? ????????????
			lhj_MemberVO.setM_mail(tempPassword);
			// setText ????????? ??????????????? ????????? ???????????? ??????
			messageHelper.setText("???????????? : " + tempPassword);
			// set ???????????? ????????? ?????????
			System.out.println("???????????? : " + tempPassword);
			// ????????? ??????
			mailSender.send(message);
			// ?????? ????????? ?????????
			System.out.println("Mail Sending End...");
			// form?????? ???????????? m_id??? ????????? setter??? ???????????? ??????????????? ?????? DB??? ???????????? DTO????????? ???????????? ??????
			ms.mail(lhj_MemberVO);
		} catch (Exception e) {
			System.out.println(e.getMessage());
			System.out.println("?????? ??????");
		}
		// ???????????? ??????
	}

	// ???????????? ??????, ????????? ?????? ??????
	@RequestMapping(value = "/member/certification", method = RequestMethod.POST)
	public String upload(AttachmentFileVO attachmentFileVO, HttpServletRequest request, String m_id, Model model)
			throws Exception {
		System.out.println("Lhj_Controller upload Start...");
		String sessionID = (String) request.getSession().getAttribute("sessionID");
		m_id = sessionID;
		AttachmentFileVO emali = new AttachmentFileVO();
		// ????????? ?????? ?????????
		emali = ms.member(attachmentFileVO);
		if (attachmentFileVO.getM_mail().equals(emali.getM_mail()) && attachmentFileVO.getFiles() != null) {
			// MultipartFile ????????? ????????? ????????? for????????? ????????? ?????? ??????
			for (MultipartFile file : attachmentFileVO.getFiles()) {
				// C:/Image/ + ?????? ?????????????????? ??????
				String originalfileName = file.getOriginalFilename();
				System.out.println("originalfileName->" + originalfileName);
				// ????????? ????????? ?????? ?????? ????????? ??????
				File data = new File("C:/Images/" + originalfileName);
				System.out.println("data->" + data);
				// ?????? ????????? ??????
				file.transferTo(data);
				// VO??? ?????? files ?????? ?????????
				System.out.println("files->" + attachmentFileVO.getFiles().size());
			}
			// ????????? ?????? DB??? ???????????? ?????? DTO ????????? Service??? ??????
			int result = ms.certification(attachmentFileVO);
			System.out.println("attachmentFileVO->m_id: " + attachmentFileVO.getM_id());
			model.addAttribute("lhj_MemberVO", attachmentFileVO);
			return "member/mypage";

		} else if (attachmentFileVO.getM_mail().equals("") || attachmentFileVO.getM_mail() != emali.getM_mail()) {
			attachmentFileVO.setM_id(m_id);
			model.addAttribute("lhj_MemberVO", attachmentFileVO);
			System.out.println("attachmentFileVO.getM_mail()->" + attachmentFileVO.getM_mail());
			System.out.println("??? ????????????");
			return "member/mypage";
		} else {
			System.out.println("??? ????????????");
			return "member/mypage";
		}
	}

	@RequestMapping(value = "/member/certification2", method = RequestMethod.POST)
	public String upload2(AttachmentFileVO attachmentFileVO, HttpServletRequest request, String m_id, Model model)
			throws Exception {
		System.out.println("Lhj_Controller upload2 Start...");
		String sessionID = (String) request.getSession().getAttribute("sessionID");
		m_id = sessionID;
		AttachmentFileVO emali = new AttachmentFileVO();
		// ????????? ?????? ?????????
		emali = ms.member(attachmentFileVO);
		if (attachmentFileVO.getM_mail().equals(emali.getM_mail()) && attachmentFileVO.getFiles() != null) {
			// ????????? ?????? DB??? ???????????? ?????? DTO ????????? Service??? ??????
			int result = ms.certification(attachmentFileVO);
			System.out.println("attachmentFileVO->m_id: " + attachmentFileVO.getM_id());
			model.addAttribute("result", result);
			model.addAttribute("lhj_MemberVO", attachmentFileVO);
		}
			return "member/mypage";
	}

}
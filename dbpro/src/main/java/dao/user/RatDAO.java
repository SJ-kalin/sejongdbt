package dao.user;

import dto.user.RatDTO;
import oracle.connect.JDBCManager;
import org.apache.commons.lang3.StringUtils;

public class RatDAO {

	private static RatDAO instance;

	private RatDAO() {
		instance = new RatDAO();
	}

	public static RatDAO getInstance() {
		return instance;
	}

	public void list() {
		final String query = "select " +
			"	MYUSER_ID, " +
			"	MOVIE_CODE, " +
			"	RAT_POINT, " +
			"	RAT_COMMENT " +
			"from " +
			"	RAT";
		JDBCManager
			.getInstance()
			.queryForList(query, RatDTO.class)
			.forEach(ratDTO -> System.out.println(ratDTO.toString()));
	}

	// 로그인한 유저의 통계 내역을 출력한다.
	public void selectRatListByUserId(String id) {
		/*
		 * movie, rat 테이블을 조인(movie_code)하고 로그인한 유저가 매긴 평점과 감상평을 출력해준다.
		 */
		final String query = "SELECT " +
			"	r.myuser_id, " +
			"	m.movie_code, " +
			"	m.movie_title, " +
			"	r.rat_point, " +
			"	r.rat_comment " +
			"FROM " +
			"	movie m " +
			"JOIN " +
			"	rat r ON m.movie_code = r.movie_code " +
			"WHERE " +
			"	r.myuser_id = " + StringUtils.wrap(id, "'");

		JDBCManager
			.getInstance()
			.queryForMaps(query, new String[] {
				"r.myuser_id",
				"m.movie_code",
				"m.movie_title",
				"r.rat_point",
				"r.rat_comment"
			}).forEach(stringObjectMap -> {
			stringObjectMap.forEach((key, value) -> System.out.print(key + ":" + value + "\t"));
			System.out.println();
		});
	}

	// 해당 영화의 평점의 평균과 감상평을 보여주는 메서드이다.
	public void selectMovieRat(String movieTitle) {

		System.out.println("원하시는 영화의 평점의 평균과 감상평을 보여드립니다.");
		System.out.println("영화 이름을 입력하세요.");

		/*
		 * subquery를 이용해서 movie table에서 movie_code를 얻은 뒤 rat 테이블에서 movie_code로 그룹핑해 평균과 평점을 매긴 회원 수를 출력한다.
		 *
		 * */
		final String movieRatingQuery = "SELECT " +
			"	AVG(RAT_POINT) AS AVG, " +
			"	COUNT(*) AS COUNT " +
			"FROM " +
			"	RAT " +
			"WHERE " +
			"	MOVIE_CODE IN (SELECT MOVIE_CODE FROM MOVIE WHERE MOVIE_TITLE= "
			+ StringUtils.wrap(movieTitle, "'") + ")" +
			"GROUP BY " +
			"	MOVIE_CODE";

		JDBCManager
			.getInstance()
			.queryForMap(movieRatingQuery, new String[] {"AVG", "COUNT"})
			.forEach((key, value) -> System.out.println(key + " : " + value));
		/*
		 * 해당영화를 본 회원들의 아이디, 평점, 감상평을 출력한다.
		 * subquery를 이용했다.
		 * */
		final String movieAudiencQuery = "SELECT " +
			"	MYUSER_ID, " +
			"	MOVIE_CODE, " +
			"	RAT_POINT, " +
			"	RAT_COMMENT " +
			"FROM " +
			"	RAT " +
			"WHERE " +
			"	MOVIE_CODE IN(SELECT MOVIE_CODE FROM MOVIE WHERE MOVIE_TITLE="
			+ StringUtils.wrap(movieTitle, "'") + ")";

		JDBCManager
			.getInstance()
			.queryForList(movieAudiencQuery, RatDTO.class)
			.forEach(ratDTO -> System.out.println(ratDTO.toString()));
	}

}

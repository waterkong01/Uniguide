package kh.BackendCapstone.dto.response;

import kh.BackendCapstone.constant.Authority;
import lombok.*;

@Getter @Setter @ToString
@NoArgsConstructor @AllArgsConstructor
public class AdminMemberResDto {
	private Long id;
	private String name;
	private String email;
	private String phone;
	private Authority authority;
	private String univName;
	private String univDept;
}

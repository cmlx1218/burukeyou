package burukeyou.search.server.temp;

import burukeyou.common.dao.pojo.BasePojo;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@TableName("ums_users")
@NoArgsConstructor
@AllArgsConstructor
public class UmsUser extends BasePojo {

	private String username;

	private String password;

	private String nickname;

	private String mobile;

	private String email;

	private String avatar;

	private String qrcode;

	private String description;

	private Boolean deleted;

	private Boolean enabled;

	private Boolean accountNonExpired;

	private Boolean credentialsNonExpired;

	private Boolean accountNonLocked;

	private String createHost;

	private String lastloginHost;

	private java.util.Date lastloginTime;

	private int fansCount;

	private int followCount;

	private String blogAddress;

	private int exp;
}

@RestController
@RequestMapping("/auth")
public class Login {
	
	private static final Logger LOGGER=LoggerFactory.getLogger(Long.class);
	
	@Autowired
	UserAuthenticationService authenticationService;
	
	@PostMapping
	public String getToken(@RequestBody User user)
	{
		LOGGER.info("Auth service");
		String token=null;
		if(authenticationService.isValiduser(user))
		{
			LOGGER.info("Valid user Generating Token");
			token=Jwts.builder().setSubject(user.getUserID()).setExpiration(new Date(System.currentTimeMillis()+SecConst.expirationTime)).signWith(SignatureAlgorithm.HS512, SecConst.secret.getBytes()).compact();
			LOGGER.info("Generated Token "+token);
			return "Auth "+token;
		}
		else
		{
			LOGGER.error("Invalid User");
			throw new CustomUnauthorizedException("Not a valid User");
		}
	}

}
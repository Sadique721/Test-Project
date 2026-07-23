package com.savbill.salescrmsbss.security.jwt;//package com.savbill.ticketmanagement.core.security.jwt;


//@Component
//public class JwtUtil {
////    private static final String CAPTIVEPORTAL = "captiveportal";
////    @Value("${jwt.token.validity}")
////    private long jwtTokenValidity;
////    @Value("${jwt.secret}")
////    private String secretKey;
////
////
////    public String extractUsername(String token) {
////	return extractClaim(token, Claims::getSubject);
////    }
////
////    public Date extractExpiration(String token) {
////	return extractClaim(token, Claims::getExpiration);
////    }
////
////    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
////	final Claims claims = extractAllClaims(token);
////	return claimsResolver.apply(claims);
////    }
////
////    private Claims extractAllClaims(String token) {
////	return Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token).getBody();
////    }
////
////    private Boolean isTokenExpired(String token) {
////	return extractExpiration(token).before(new Date());
////    }
////
////    public String generateToken(UserDetails userDetails) {
////	Map<String, Object> claims = new HashMap<>();
////	return createToken(claims, userDetails.getUsername());
////    }
////
////    private String createToken(Map<String, Object> claims, String subject) {
////	if (subject.equalsIgnoreCase(CAPTIVEPORTAL)) {
////	    return Jwts.builder().setClaims(claims).setSubject(subject)
////		    .setIssuedAt(new Date(System.currentTimeMillis()))
////		    .setExpiration(new Date(System.currentTimeMillis()))
////		    .signWith(SignatureAlgorithm.HS256, secretKey).compact();
////	} else {
////	    return Jwts.builder().setClaims(claims).setSubject(subject)
////		    .setIssuedAt(new Date(System.currentTimeMillis()))
////		    .setExpiration(new Date(System.currentTimeMillis() + jwtTokenValidity))
////		    .signWith(SignatureAlgorithm.HS256, secretKey).compact();
////	}
////    }
////
////    public String doGenerateRefreshToken(Map<String, Object> claims, String subject) {
////
////	return Jwts.builder().setClaims(claims).setSubject(subject).setIssuedAt(new Date(System.currentTimeMillis()))
////		.setExpiration(new Date(System.currentTimeMillis()))
////		.signWith(SignatureAlgorithm.HS512, secretKey).compact();
////
////    }
////
////    public Boolean validateToken(String token, UserDetails userDetails) {
////	final String username = extractUsername(token);
////	return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
////    }
////
////    /*
////     * public List<SimpleGrantedAuthority> getRolesFromToken(String token) { Claims
////     * claims =
////     * Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token).getBody();
////     *
////     * List<SimpleGrantedAuthority> roles = null;
////     *
////     * Boolean isAdmin = claims.get("isAdmin", Boolean.class); Boolean isUser =
////     * claims.get("isUser", Boolean.class);
////     *
////     * if (isAdmin != null && isAdmin) { roles = Arrays.asList(new
////     * SimpleGrantedAuthority("ROLE_ADMIN")); }
////     *
////     * if (isUser != null && isAdmin) { roles = Arrays.asList(new
////     * SimpleGrantedAuthority("ROLE_USER")); } return roles;
////     *
////     * }
////     */
//}

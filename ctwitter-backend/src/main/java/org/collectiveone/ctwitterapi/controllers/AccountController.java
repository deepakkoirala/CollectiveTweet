package org.collectiveone.ctwitterapi.controllers;

import java.util.List;

import org.collectiveone.ctwitterapi.dtos.AccountDto;
import org.collectiveone.ctwitterapi.dtos.TweetDto;
import org.collectiveone.ctwitterapi.dtos.UserThumbnailDto;
import org.collectiveone.ctwitterapi.model.Account;
import org.collectiveone.ctwitterapi.model.AccountState;
import org.collectiveone.ctwitterapi.repositories.AccountRepositoryIf;
import org.collectiveone.ctwitterapi.services.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.social.twitter.api.Tweet;
import org.springframework.social.twitter.api.impl.TwitterTemplate;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("1/secured/account")
@RestController
public class AccountController {

	@Value("${twitter.consumer.key}")
    String consumerKey;

    @Value("${twitter.consumer.key.secret}")
    String consumerSecret;
    
    @Autowired
    AccountService accountService;
    
    @Autowired
    AccountRepositoryIf accountRepository;
	
    @RequestMapping(path = "mines", method = RequestMethod.GET)
    public @ResponseBody List<AccountDto> mines() {
    	Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    	return accountService.getOfUser(auth.getName());
    }  
    
    @RequestMapping(path = "/{id}",  method = RequestMethod.GET)
    public AccountDto get(@PathVariable("id") Long id) {
    	return accountService.get(id);
    }
    
    @RequestMapping(path = "/{id}/members",  method = RequestMethod.GET)
    public List<UserThumbnailDto> getMembers(@PathVariable("id") Long id) {
    	return accountService.getMembers(id);
    }
    
    @RequestMapping(path = "/{id}/addMember",  method = RequestMethod.POST)
    public String addMember(@PathVariable("id") Long accountId, @RequestParam("userId") Long userId) {
    	return accountService.addMember(accountId,userId);
    }
    
	@RequestMapping(path = "{id}/tweet", method = RequestMethod.POST, produces = { MediaType.TEXT_HTML_VALUE })
    public String tweet(@PathVariable("id") Long id, @RequestBody TweetDto tweetDto) {
		
		Account account = accountRepository.findById(id);

		if(account != null) {
			if(account.getState() == AccountState.AUTHORIZED) {
				TwitterTemplate twitterTemplate = new TwitterTemplate(consumerKey, consumerSecret, account.getAccessToken(), account.getAccessTokenSecret());
		        twitterTemplate.timelineOperations()
		            .updateStatus(tweetDto.getText());
		        
		        return "done";
			}
		}
		
		return "error";
		
	}
	
	@RequestMapping(path = "{id}/recentTweets", method = RequestMethod.GET)
    public List<Tweet> recentTweets(@PathVariable("id") Long id) {
		
		Account account = accountRepository.findById(id);

		TwitterTemplate twitterTemplate = new TwitterTemplate(consumerKey, consumerSecret, account.getAccessToken(), account.getAccessTokenSecret());
        List<Tweet> list = twitterTemplate.timelineOperations().getHomeTimeline();
        
        return list;
	}
}

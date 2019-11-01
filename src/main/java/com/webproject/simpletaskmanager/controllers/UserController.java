package com.webproject.simpletaskmanager.controllers;

import java.sql.Timestamp;
import java.util.Date;
import java.util.Enumeration;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttribute;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.bind.support.SessionStatus;

import com.webproject.simpletaskmanager.entities.Task;
import com.webproject.simpletaskmanager.entities.UserInfo;
import com.webproject.simpletaskmanager.entities.Useraccount;
import com.webproject.simpletaskmanager.forms.UserEditForm;
import com.webproject.simpletaskmanager.repositories.TaskRepository;
import com.webproject.simpletaskmanager.repositories.UserRepository;
import com.webproject.simpletaskmanager.repositoriesdao.TaskDAO;
import com.webproject.simpletaskmanager.repositoriesdao.UseraccountDAO;

@Transactional
@Controller("/dashboard")
@SessionAttributes("user")
public class UserController {	

	@Autowired
	TaskRepository taskRepository;
	
	@Autowired
	UserRepository userRepository;
	
	@RequestMapping(value="/dashboard", method=RequestMethod.GET)
	public String dashboardPage(HttpServletRequest request, Model model) {
		Useraccount user = (Useraccount) model.asMap().get("user");
		System.out.println("Logged in user = " + user);
		model.addAttribute("user", user);
		return "dashboard";
	}
	
	//Creates a new Task
	@RequestMapping(value="/addTask", method=RequestMethod.POST)
	public String addTask(@ModelAttribute("name") String taskName, @SessionAttribute("user") Useraccount loggedInUser,
			SessionStatus status, Model model) {
		Task task = new Task();
		task.setName(taskName);
		task.setStatus(false);
		task.setCreated(new Timestamp(new Date().getTime()));
		loggedInUser.addTask(task);
		taskRepository.save(task);
		System.out.println("Added new task to " + loggedInUser);
		return "redirect:/dashboard";
	}
	
	//Deletes a task
	@RequestMapping(value="/deleteTask/{id}", method=RequestMethod.GET)
	public String deleteTask(@PathVariable("id") Integer taskId, @SessionAttribute("user") Useraccount loggedInUser, SessionStatus status, Model model) {
		Task existingTask = taskRepository.findTaskById(taskId);
		if (loggedInUser.removeTask(existingTask)) {
			taskRepository.deleteTaskById(taskId);
		}
		return "redirect:/dashboard";
	}
	
	//TODO: Edit task
	
	//TODO: Filter By name, Sort by name, sort by date 
	
	//Edits the user info
	@RequestMapping(value="/user_edit_form", method=RequestMethod.GET)
	public String editPage(Model model, @SessionAttribute Useraccount user) {
		model.addAttribute("userInfo", user);
		return "user_edit_form";
	}
	
	//Updates the user info
	//TODO: Validate form
	@RequestMapping(value="/updateUser", method=RequestMethod.POST)
	public String editUser(@ModelAttribute("userInfo") Useraccount userEdit, @SessionAttribute("user") Useraccount loggedInUser, Model model) {
		String username = userEdit.getUsername();
		String password = userEdit.getPassword();
		String firstName = userEdit.getUserInfo().getFirstName();
		String lastName = userEdit.getUserInfo().getLastName();
		String personalEmail = userEdit.getUserInfo().getPersonalEmail();
		
		loggedInUser.setUsername(username);
		loggedInUser.setPassword(password);
		loggedInUser.getUserInfo().setFirstName(firstName);
		loggedInUser.getUserInfo().setLastName(lastName);
		loggedInUser.getUserInfo().setPersonalEmail(personalEmail);

		userRepository.save(loggedInUser);
		return "redirect:/dashboard";
	}
	
	
	//Logs the user out
	@RequestMapping(value="/endsession", method=RequestMethod.POST)
	public String logout(SessionStatus status, @SessionAttribute("user") Useraccount loggedInUser) {
		status.setComplete();
		System.out.println(loggedInUser + " has logged out");
		return "redirect:/login";
	}
}

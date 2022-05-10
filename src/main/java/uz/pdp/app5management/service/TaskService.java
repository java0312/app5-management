package uz.pdp.app5management.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import uz.pdp.app5management.entity.Role;
import uz.pdp.app5management.entity.Task;
import uz.pdp.app5management.entity.User;
import uz.pdp.app5management.entity.enums.RoleName;
import uz.pdp.app5management.payload.ApiResponse;
import uz.pdp.app5management.payload.TaskDto;
import uz.pdp.app5management.repository.TaskRepository;
import uz.pdp.app5management.repository.UserRepository;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class TaskService {

    @Autowired
    TaskRepository taskRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    JavaMailSender javaMailSender;

    //__CREATE
    public ApiResponse addTask(TaskDto taskDto) {
        /*
         *
         *
         *
         * FROM USER AUTHENTICATION ICHIDAN OLINADI
         *
         *
         * */
        Optional<User> optionalToUser = userRepository.findById(taskDto.getToUserId());
        if (optionalToUser.isPresent()) {

            User principal = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            User toUser = optionalToUser.get();

            Role fromUserRole = (Role) principal.getRoles().toArray()[0];
            Role toUserRole = (Role) toUser.getRoles().toArray()[0];

            /*
             * Xodim vazifa biriktira olmaydi
             * */
            if (fromUserRole.getRoleName().equals(RoleName.WORKER))
                return new ApiResponse("You cannot give a task", false);

            /*
             * manager managerga yoki directorga vazifa beraolmaydi
             * */
            if (fromUserRole.getRoleName().equals(RoleName.HR_MANAGER) &&
                    (toUserRole.getRoleName().equals(RoleName.DIRECTOR) || toUserRole.getRoleName().equals(RoleName.HR_MANAGER)))
                return new ApiResponse("You cannot give a task", false);

            Task task = new Task();
            task.setCreatedDate(new Date());
            task.setExpiredDate(taskDto.getExpireDate());
            task.setName(taskDto.getName());
            task.setText(taskDto.getText());
            task.setToUser(toUser);
            task.setFromUser(principal);
            Task savedTask = taskRepository.save(task);

            boolean sendEmail = sendEmail(savedTask);
            if (sendEmail)
                return new ApiResponse("Task is sent", true);
            return new ApiResponse("Error sending task", false);
        }
        return new ApiResponse("ToUser not found!", false);
    }

    private boolean sendEmail(Task task) {
        try {
            SimpleMailMessage mailMessage = new SimpleMailMessage();
            mailMessage.setFrom(task.getFromUser().getEmail());
            mailMessage.setTo(task.getToUser().getEmail());
            mailMessage.setSubject("TASK: " + task.getName());
            mailMessage.setText(
                    task.getText() +
                            "\n\tI have done -> http://localhost:8080/api/task/done?id=" + task.getId()
            );
            javaMailSender.send(mailMessage);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    //task done
    public ApiResponse taskDone(UUID id) {
        Optional<Task> optionalTask = taskRepository.findById(id);
        if (optionalTask.isPresent()) {
            Task task = optionalTask.get();

            task.setFinishedDate(new Date());
            task.setDone(true);
            taskRepository.save(task);
            boolean done = sendEmailAboutDone(task);
            return new ApiResponse("Task done!", true);
        }
        return new ApiResponse("Task not found!", false);
    }

    public boolean sendEmailAboutDone(Task task) {
        try {
            SimpleMailMessage mailMessage = new SimpleMailMessage();
            mailMessage.setFrom(task.getToUser().getEmail());
            mailMessage.setTo(task.getFromUser().getEmail());
            mailMessage.setSubject("Done");
            mailMessage.setText(
                    task.getText() +
                            "\n\tI have just done"
            );
            javaMailSender.send(mailMessage);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public Task getTaskById(UUID id) {
        Optional<Task> optionalTask = taskRepository.findById(id);
        if (optionalTask.isPresent()) {
            User user = getUser();
            Role role = (Role) user.getRoles().toArray()[0];
            if (role.getRoleName().equals(RoleName.DIRECTOR) || role.getRoleName().equals(RoleName.HR_MANAGER)) {
                return optionalTask.get();
            }
        }
        return null;
    }

    private User getUser() {
        return ((User) SecurityContextHolder.getContext().getAuthentication().getPrincipal());
    }

    public List<Task> getAllTasks() {
        User user = getUser();
        Role role = (Role) user.getRoles().toArray()[0];
        if (role.getRoleName().equals(RoleName.DIRECTOR))
            return taskRepository.findAll();
        if (role.getRoleName().equals(RoleName.HR_MANAGER))
            return taskRepository.findAllByFromUser(user);
        return null;
    }

    public ApiResponse editTask(UUID id, TaskDto taskDto) {

        User user = getUser();
        Optional<Task> optionalTask = taskRepository.findById(id);
        if (optionalTask.isPresent()){
            Task editingTask = optionalTask.get();
            if (user.getEmail().equals(editingTask.getFromUser().getEmail())){
                editingTask.setName(taskDto.getName());
                editingTask.setExpiredDate(taskDto.getExpireDate());
                editingTask.setText(taskDto.getText());
                taskRepository.save(editingTask);
                return new ApiResponse("Task edited!", true);
            }
            return new ApiResponse("This task is not yours", false);
        }
        return new ApiResponse("Task not found!", false);

    }

    public ApiResponse deleteTask(UUID id) {

        User user = getUser();
        Optional<Task> optionalTask = taskRepository.findById(id);
        if (optionalTask.isPresent()){
            if (user.getEmail().equals(optionalTask.get().getFromUser().getEmail())){
                taskRepository.deleteById(id);
                return new ApiResponse("Task deleted!", true);
            }
            return new ApiResponse("This task is not yours", false);
        }
        return new ApiResponse("Task not found!", false);

    }

    public List<Task> getTasksByWorker() {
        User user = getUser();
        return taskRepository.findAllByToUser(user);
    }

    public List<Task> getNotDoneOnTimeTasks() {
        User user = getUser();
        Role role = (Role) user.getRoles().toArray()[0];
        if (role.getRoleName().equals(RoleName.DIRECTOR) || role.getRoleName().equals(RoleName.HR_MANAGER)){
            List<Task> all = taskRepository.findAll();
            List<Task> tasks = null;
            for (Task task : all) {
                if (task.getExpiredDate().getTime() < task.getFinishedDate().getTime()){
                    tasks.add(task);
                }
            }
            return tasks;
        }
        return null;
    }
}

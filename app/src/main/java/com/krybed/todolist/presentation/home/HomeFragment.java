package com.krybed.todolist.presentation.home;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.core.view.MenuHost;
import androidx.core.view.MenuProvider;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

//import com.example.todolist.R;
//import com.example.todolist.activity.EditTaskActivity;
//import com.example.todolist.databinding.FragmentHomeBinding;
//import com.example.todolist.domain.model.Attachment;
//import com.example.todolist.domain.model.Task;
//import com.example.todolist.domain.model.enums.NotificationType;
//import com.example.todolist.domain.model.enums.Priority;
//import com.example.todolist.domain.model.enums.SortType;
//import com.example.todolist.presentation.home.adapter.TaskAdapter;
//import com.example.todolist.presentation.viewmodel.TaskViewModel;
//import com.example.todolist.util.file.FileService;
//import com.example.todolist.util.lang.LocalHelper;
//import com.example.todolist.util.notification.NotificationUtils;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

public class HomeFragment extends Fragment {

//    private FragmentHomeBinding binding;
//    private TaskAdapter taskAdapter;
//    private TaskViewModel taskViewModel;
//    private Task currentAttachmentTask = null;
//    private ActivityResultLauncher<Intent> attachmentPickerLauncher;
//
//    @Override
//    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
//                             Bundle savedInstanceState) {
//        binding = FragmentHomeBinding.inflate(inflater, container, false);
//        NotificationUtils.createNotificationChannel(requireContext());
//
//        initRecyclerView();
//        initAttachmentPicker();
//
//        taskViewModel = new ViewModelProvider(requireActivity()).get(TaskViewModel.class);
////        taskViewModel.deleteAll(this::insertDummyTasks);
//        taskViewModel.getTasks().observe(getViewLifecycleOwner(), tasks -> {
//            taskAdapter.updateTasks(tasks);
//
//            taskViewModel.getHasInsertedDummy().observe(getViewLifecycleOwner(), hasInserted -> {
//                if ((hasInserted == null || !hasInserted) && tasks.isEmpty()) {
//                    insertDummyTasks();
//                    taskViewModel.markDummyInserted();
//                }
//            });
//
//            taskViewModel.getNotificationChecked().observe(getViewLifecycleOwner(), checked -> {
//                if (checked != null) {
//                    checkForUpcomingDeadlines(tasks, checked);
//                    if (!checked) {
//                        taskViewModel.markNotificationChecked();
//                    }
//                }
//            });
//        });
//        return binding.getRoot();
//    }
//
//    @Override
//    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
//        super.onViewCreated(view, savedInstanceState);
//        MenuHost menuHost = requireActivity();
//
//        menuHost.addMenuProvider(new MenuProvider() {
//            @Override
//            public void onCreateMenu(@NonNull Menu menu, @NonNull MenuInflater menuInflater) {
//                menuInflater.inflate(R.menu.sort_menu, menu);
//
//                MenuItem item = menu.findItem(R.id.sort_spinner_item);
//                Spinner spinner = (Spinner) item.getActionView();
//                if (spinner != null) {
//                    List<String> sortDisplayNames = Arrays.stream(SortType.values())
//                            .map(type -> getString(type.getStringResId()))
//                            .collect(Collectors.toList());
//                    ArrayAdapter<String> adapter = new ArrayAdapter<>(
//                            requireContext(),
//                            android.R.layout.simple_spinner_item,
//                            sortDisplayNames
//                    );
//                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//                    spinner.setAdapter(adapter);
//                    spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//                        @Override
//                        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//                            String selectedName = (String) parent.getItemAtPosition(position);
//                            SortType selected = SortType.fromDisplayName(getContext(), selectedName);
//                            List<Task> currentTasks = taskViewModel.getTasks().getValue();
//                            if (currentTasks != null) {
//                                taskViewModel.sortTasks(new ArrayList<>(currentTasks), selected);
//                            }
//                        }
//                        @Override
//                        public void onNothingSelected(AdapterView<?> parent) {}
//                    });
//                }
//
//                MenuItem langItem = menu.findItem(R.id.current_lang_flag);
//                String lang = Locale.getDefault().getLanguage();
//                if (lang.equals("pl")) {
//                    langItem.setIcon(R.drawable.ic_polish);
//                }
//                else if (lang.equals("en")) {
//                    langItem.setIcon(R.drawable.ic_english);
//                }
//            }
//
//            @Override
//            public boolean onMenuItemSelected(@NonNull MenuItem menuItem) {
//                if (menuItem.getItemId() == R.id.current_lang_flag) {
//                    LocalHelper.showChangeLanguageDialog(requireContext());
//                    return true;
//                }
//                return false;
//            }
//        }, getViewLifecycleOwner());
//    }
//
//    private void initRecyclerView() {
//        binding.tasksRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
//        taskAdapter = new TaskAdapter(new ArrayList<>(), new TaskAdapter.OnTaskClickListener() {
//            @Override
//            public void onEditClick(Task task) {
//                Log.i("Task", "Edit: " + task.getId() + "." + task.getTitle());
//                startActivity(
//                        new Intent(getContext(), EditTaskActivity.class)
//                                .putExtra("taskId", task.getId())
//                );
//            }
//
//            @Override
//            public void onDeleteClick(Task task) {
//                new AlertDialog.Builder(getContext())
//                        .setTitle(getString(R.string.confirm_delete_title))
//                        .setMessage(getString(R.string.confirm_delete_task_message) +  "\"" + task.getTitle() + "\"?")
//                        .setPositiveButton(getString(R.string.yes), (dialog, which) -> {
//                            Log.i("Delete task", task.getId() + ". " + task.getTitle());
//                            taskViewModel.delete(task);
//                        })
//                        .setNegativeButton(getString(R.string.no), (dialog, which) -> {
//                            dialog.dismiss();
//                        })
//                        .show();
//            }
//
//            @Override
//            public void onChangeStatusClick(Task task) {
//                Log.i("Change task status", task.getId() + "." + task.getTitle());
//                taskViewModel.changeStatus(task);
//            }
//
//            @Override
//            public void onLongClick(Task task) {
//                Log.i("Change task status", task.getId() + "." + task.getTitle());
//                taskViewModel.changeStatus(task);
//            }
//
//            @Override
//            public void onAddAttachmentClick(Task task) {
//                Log.i("Add attachment", task.getId() + "." + task.getTitle());
//                currentAttachmentTask = task;
//
//                Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
//                intent.setType("*/*");
//                intent.addCategory(Intent.CATEGORY_OPENABLE);
//                intent.putExtra(Intent.EXTRA_MIME_TYPES, new String[]{"image/*", "video/*", "application/pdf"});
//                attachmentPickerLauncher.launch(intent);
//            }
//
//            @Override
//            public void onDeleteAttachmentClick(Task task) {
//                if (task.getAttachments().isEmpty()) {
//                    Toast.makeText(getContext(), getString(R.string.no_attachments_toast), Toast.LENGTH_SHORT).show();
//                    return;
//                }
//
//                String[] attachmentsName = task.getAttachments().stream()
//                        .map(Attachment::getFilename)
//                        .toArray(String[]::new);
//
//                new AlertDialog.Builder(getContext())
//                        .setTitle(getString(R.string.delete_attachment_alertdialog_title))
//                        .setItems(attachmentsName, (dialog, which) -> {
//                            Attachment selected = task.getAttachments().get(which);
//
//                            new AlertDialog.Builder(getContext())
//                                    .setTitle(getString(R.string.confirm_delete_title))
//                                    .setMessage(getString(R.string.delete_attachment_alertdialog_message))
//                                    .setPositiveButton(getString(R.string.yes), (dialogA, whichA) -> {
//                                        Log.i("Delete attachment", selected.getFilename());
//                                        deleteAttachment(task, selected);
//                                    })
//                                    .setNegativeButton(getString(R.string.no), (dialogA, whichA) -> {
//                                        dialog.dismiss();
//                                    })
//                                    .show();
//                        })
//                        .setNegativeButton(getString(R.string.cancel), (dialog, which) -> dialog.dismiss())
//                        .show();
//            }
//
//            @Override
//            public void onShowAttachmentClick(Task task) {
//                if (task.getAttachments().isEmpty()) {
//                    Toast.makeText(getContext(), getString(R.string.no_attachments_toast), Toast.LENGTH_SHORT).show();
//                    return;
//                }
//
//                String[] attachmentsName = task.getAttachments().stream()
//                        .map(Attachment::getFilename)
//                        .toArray(String[]::new);
//
//                new AlertDialog.Builder(getContext())
//                        .setTitle(getString(R.string.select_attachment))
//                        .setItems(attachmentsName, (dialog, which) -> {
//                            Attachment selected = task.getAttachments().get(which);
//                            openAttachment(requireContext(), selected);
//                        })
//                        .setNegativeButton(getString(R.string.cancel), (dialog, which) -> dialog.dismiss())
//                        .show();
//            }
//        });
//
//        binding.tasksRecyclerView.setAdapter(taskAdapter);
//    }
//
//    private void initAttachmentPicker() {
//        attachmentPickerLauncher = registerForActivityResult(
//                new ActivityResultContracts.StartActivityForResult(),
//                result -> {
//                    if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
//                        Uri sourceUri = result.getData().getData();
//                        if (sourceUri == null) {
//                            return;
//                        }
//
//                        String filename = FileService.getFileNameFromUri(getContext(), sourceUri);
//                        Uri localUri = FileService.copyFileToInternalStorage(requireContext(), sourceUri, filename);
//
//                        if (localUri != null && currentAttachmentTask != null) {
//                            Log.i("Attachment", "Selected file: " + localUri);
//                            Attachment attachment = new Attachment(
//                                    currentAttachmentTask.getId(),
//                                    filename,
//                                    localUri.toString()
//                            );
//                            taskViewModel.addAttachmentToTask(attachment);
//                            Toast.makeText(getContext(), getString(R.string.attachment_added), Toast.LENGTH_SHORT).show();
//                        }
//                    }
//                }
//        );
//    }
//
//    private void insertDummyTasks() {
//        taskViewModel.insert(new Task("Do the shopping",
//                LocalDateTime.now().plusDays(5).with(LocalTime.of(12, 0)), true, Priority.HIGH, LocalDateTime.now()));
//        taskViewModel.insert(new Task("Gym session",
//                LocalDateTime.now().plusDays(3).with(LocalTime.of(14, 30)), false, Priority.MEDIUM, LocalDateTime.now()));
//        taskViewModel.insert(new Task("Team meeting",
//                LocalDateTime.now().plusDays(15).with(LocalTime.of(7, 25)), false, Priority.MEDIUM, LocalDateTime.now()));
//        taskViewModel.insert(new Task("Dentist appointment",
//                LocalDateTime.now().plusDays(25).with(LocalTime.of(9, 50)), false, Priority.LOW, LocalDateTime.now()));
//        taskViewModel.insert(new Task("Project deadline",
//                LocalDateTime.now().plusDays(2).with(LocalTime.of(18, 0)), true, Priority.LOW, LocalDateTime.now()));
//        taskViewModel.insert(new Task("Finish reading the book",
//                LocalDateTime.now().with(LocalTime.of(23, 59)), false, Priority.HIGH, LocalDateTime.now()));
//        taskViewModel.insert(new Task("Water the plants",
//                LocalDateTime.now().minusDays(3), false, Priority.HIGH, LocalDateTime.now()));
//    }
//
//    private void openAttachment(Context context, Attachment attachment) {
//        try {
//            Uri uri = Uri.parse(attachment.getFilePath());
//            Intent intent = new Intent(Intent.ACTION_VIEW);
//            intent.setDataAndType(uri, context.getContentResolver().getType(uri));
//            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
//
//            context.startActivity(intent);
//        }
//        catch (ActivityNotFoundException e) {
//            Toast.makeText(context, context.getString(R.string.no_app_to_open_file), Toast.LENGTH_SHORT).show();
//        }
//        catch (Exception e) {
//            Log.e("Open attachment error", Log.getStackTraceString(e));
//        }
//    }
//
//    private void deleteAttachment(Task task, Attachment attachment) {
//        if (FileService.deleteFileFromInternalStorage(requireContext(), attachment.getFilePath())) {
//            taskViewModel.deleteAttachment(attachment);
//            task.getAttachments().removeIf(a -> a.getId() == attachment.getId());
//            Toast.makeText(getContext(), getString(R.string.attachment_deleted), Toast.LENGTH_SHORT).show();
//        }
//        else {
//            Toast.makeText(getContext(), getString(R.string.error_deleting_attachment), Toast.LENGTH_SHORT).show();
//        }
//    }
//
//    private void checkForUpcomingDeadlines(List<Task> tasks, boolean isNotificationShown) {
//        LocalDateTime now = LocalDateTime.now();
//        LocalDateTime threshold = now.plusHours(24);
//        List<Task> tasksToNotify = new ArrayList<>();
//
//        for (Task task : tasks) {
//            if (!task.isDone()) {
//                if (task.getDeadline().isBefore(now)) {
//                    task.setNotificationType(NotificationType.OVERDUE);
//                    tasksToNotify.add(task);
//                    if (!isNotificationShown) {
//                        NotificationUtils.showTaskNotification(requireContext(), task);
//                    }
//                }
//                else if (task.getDeadline().isAfter(now) && task.getDeadline().isBefore(threshold)) {
//                    task.setNotificationType(NotificationType.UPCOMING);
//                    tasksToNotify.add(task);
//                    if (!isNotificationShown) {
//                        NotificationUtils.showTaskNotification(requireContext(), task);
//                    }
//                }
//            }
//        }
//        taskViewModel.updateTasksForNotification(tasksToNotify);
//    }
//
//    @Override
//    public void onDestroyView() {
//        super.onDestroyView();
//        binding = null;
//    }
}
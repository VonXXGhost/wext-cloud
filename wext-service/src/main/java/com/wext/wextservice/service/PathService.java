package com.wext.wextservice.service;

import com.wext.common.domain.exception.InvalidOperationException;
import com.wext.common.domain.exception.NonExistentException;
import com.wext.common.utils.WextTool;
import com.wext.wextservice.domain.Path;
import com.wext.wextservice.domain.PathCount;
import com.wext.wextservice.repository.PathCountRepository;
import com.wext.wextservice.repository.PathRepository;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.regex.Pattern;

@Service
@Slf4j
public class PathService {

    private PathRepository pathRepository;
    private PathCountRepository pathCountRepository;

    @Autowired
    public PathService(PathRepository pathRepository, PathCountRepository pathCountRepository) {
        this.pathRepository = pathRepository;
        this.pathCountRepository = pathCountRepository;
    }

    public Path getPath(@NonNull Long id) {
        return pathRepository.findById(id)
                .orElseThrow(() -> new NonExistentException("Can not find this path."));
    }

    public Path getPath(@NonNull String fullPath) {
        return pathRepository.findByFullPath(fullPath)
                .orElseThrow(() -> new NonExistentException("Can not find the path of " + fullPath));
    }

    public List<Path> getPathsByPrefix(@NonNull String prePath,
                                      Integer page, Integer pageSize) {
        if (page == null || page < 1) {
            page = 1;
        }
        if (pageSize == null || pageSize < 1) {
            pageSize = 30;
        }
        Pageable pageable = PageRequest.of(page - 1, pageSize);

        return pathRepository.findAllByFullPathIsStartingWith(prePath, pageable).getContent();
    }

    public List<Path> getChildPaths(@NonNull Long parentId,
                                   Integer page, Integer pageSize) {
        if (page == null || page < 1) {
            page = 1;
        }
        if (pageSize == null || pageSize < 1) {
            pageSize = 30;
        }
        Pageable pageable = PageRequest.of(page - 1, pageSize);

        return pathRepository.findAllByParentId(parentId, pageable).getContent();
    }

    public Path createPath(@NonNull String nodeName, Long parentId) {
        if (parentId == null) {
            parentId = 1L;  // 1为根节点
        }
        Path parent;
        if (parentId != 1L) {
            parent = pathRepository.findById(parentId)
                    .orElseThrow(() -> new NonExistentException("Can not find the parent path."));  // 存在性检查
        } else {
            parent = Path.builder().id(1L).fullPath("").build();
        }

        if (!Pattern.matches("^[a-z_\\d]+$", nodeName)) {   // 节点名合法性检查
            throw new InvalidOperationException("Invalid node name.");
        }
        String fullPath = parent.getFullPath() + "/" + nodeName;
        if (pathRepository.findByFullPath(fullPath).isPresent()) {  // 存在检查
            throw new InvalidOperationException("The node is exist.");
        }

        return pathRepository.save(Path.builder()
                .nodeName(nodeName)
                .fullPath(fullPath)
                .parentId(parentId).build());

    }

    public Path createPath(@NonNull String fullPath) {
        var paths = WextTool.splitPath(fullPath);
        if (paths.size() < 2) {
            throw new InvalidOperationException("Path length is too short.");
        }
        var parentFullPath = paths.get(paths.size() - 2);
        var nodeName = fullPath.substring(fullPath.lastIndexOf("/"));
        var parentPath = getPath(parentFullPath);
        return createPath(nodeName, parentPath.getId());
    }


    /**
     * @param fullPath 全路径
     * @param hours 时间指定，选取多少小时之前的数据统计
     */
    public List<PathCount> getHotChildPaths(@NonNull String fullPath, @NonNull Integer hours,
                                            Integer page, Integer pageSize) {
        if (fullPath.equals("/")) {
            fullPath = "";
        }
        var res = pathCountRepository.getHotChildPath(fullPath, hours, PageRequest.of(page - 1, pageSize));
        log.debug(res.toString());
        return res.getContent();
    }
}

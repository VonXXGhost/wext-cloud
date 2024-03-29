package com.wext.common.domain.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ListRequest<T> implements Serializable {

    private static final long serialVersionUID = -6986746375915710855L;

    private List<T> list;
}

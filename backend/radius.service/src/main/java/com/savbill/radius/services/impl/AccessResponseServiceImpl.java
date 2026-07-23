package com.savbill.radius.services.impl;

import com.savbill.radius.dto.AccessResponseDto;
import com.savbill.radius.entity.AccessResponse;
import com.savbill.radius.repository.AccessResponseRepo;
import com.savbill.radius.services.AccessResponseService;
import com.savbill.radius.utils.CustomValidationException;
import com.savbill.radius.utils.PaginationRequestDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.Optional;


@Service
public class AccessResponseServiceImpl implements AccessResponseService {

    @Autowired
    private AccessResponseRepo accessResponseRepo;

    @Override
    public AccessResponse saveAccessResponse(AccessResponseDto accessResponseDto) {
        AccessResponse accessResponse = new AccessResponse();
        try{
            Integer count = accessResponseRepo.duplicateVerifyAtSave(accessResponseDto.getName().trim());
            if (count==0) {
                accessResponse.setId(accessResponseDto.getId());
                accessResponse.setName(accessResponseDto.getName().trim());
                accessResponse.setMessage(accessResponseDto.getMessage());
                accessResponse.setEvent(accessResponseDto.getEvent());
                accessResponse.setIsDelete(false);
                accessResponse = accessResponseRepo.save(accessResponse);
                System.out.println(accessResponse);
            }else {
                throw new CustomValidationException(HttpStatus.NOT_ACCEPTABLE.value(),"Name Already Exists", null );
            }
        }catch (CustomValidationException ce) {
            throw new CustomValidationException(HttpStatus.BAD_REQUEST.value(), ce.getMessage(), null);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return  accessResponse;
    }

    @Override
    public AccessResponse updateAccessResponse(AccessResponseDto accessResponseDto) {
        Optional<AccessResponse> accessResponse= accessResponseRepo.findById(accessResponseDto.getId());
        try{
            if (!accessResponse.isPresent()){
                throw new CustomValidationException(HttpStatus.NOT_ACCEPTABLE.value(),"Name Not Exists", null );
            }
            Integer count = accessResponseRepo.duplicateVerifyAtSave(accessResponseDto.getName().trim());
            if (count==0 || count==1) {
                accessResponse.get().setName(accessResponseDto.getName().trim());
                accessResponse.get().setMessage(accessResponseDto.getMessage());
                accessResponse.get().setEvent(accessResponseDto.getEvent());
                accessResponse.get().setIsDelete(false);
                accessResponseRepo.save(accessResponse.get());
            }else {
                throw new CustomValidationException(HttpStatus.NOT_ACCEPTABLE.value(),"Name Already Exists", null );
            }
        }catch (CustomValidationException ce) {
            throw new CustomValidationException(HttpStatus.BAD_REQUEST.value(), ce.getMessage(), null);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return accessResponse.get();
    }

    @Override
    public AccessResponse findAccessResponsebyId(Long id) {
        AccessResponse accessResponse = new AccessResponse();
        try{
            accessResponse = accessResponseRepo.findById(id).get();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
            return accessResponse;
    }

    @Override
    public Page<AccessResponse> findAccessResponse(PaginationRequestDTO requestDTO) {
        Page<AccessResponse> accessResponse = null;
        try{
            Pageable pageable = PageRequest.of(requestDTO.getPage()-1, requestDTO.getPageSize());
            accessResponse = accessResponseRepo.findAllByIsDeleteFalse(pageable);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return accessResponse;
    }

    @Override
    public AccessResponse deleteAccessResponsebyId(Long id) {
        Optional<AccessResponse> accessResponse = accessResponseRepo.findById(id);
        try{

            if (accessResponse.isPresent()){
                accessResponse.get().setIsDelete(true);
                accessResponseRepo.save(accessResponse.get());
                return accessResponse.get();
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return accessResponse.get();
    }


}

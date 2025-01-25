package learningFlow.learningFlow_BE.converter;

import learningFlow.learningFlow_BE.web.dto.collection.CollectionResponseDTO;
import learningFlow.learningFlow_BE.web.dto.home.HomeResponseDTO;

import java.util.List;

public class HomeConverter {

    public static HomeResponseDTO.GuestHomeInfoDTO convertToGuestHomeInfoDTO(
            List<CollectionResponseDTO.CollectionPreviewDTO> collectionPreviewList
    ) {
        return HomeResponseDTO.GuestHomeInfoDTO.builder()
                .recommendedCollections(collectionPreviewList)
                .build();
    }
}
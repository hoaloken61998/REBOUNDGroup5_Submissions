//package com.rebound.data;
//import com.rebound.R;
//import com.rebound.models.Main.NewsDetailItem;
//import com.rebound.models.Main.NewsItem;
//import java.util.ArrayList;
//import java.util.List;
//public class NewsData {
//
//    public static List<NewsDetailItem> getSampleNews() {
//        List<NewsDetailItem> list = new ArrayList<>();
//
//        list.add(new NewsDetailItem(
//                "Pocket These Quick Tips for Cleaning Your Ear Piercings",
//                "ục Quản lý Dược (Bộ Y tế) vừa thu hồi gần 300 số tiếp nhận phiếu công bố sản phẩm mỹ phẩm, bao gồm kem chống nắng, sữa rửa mặt, sữa tắm, nước tẩy trang của 7 doanh nghiệp.\n" +
//                        "Chỉ trong ngày 29/5, Cục Quản lý Dược (Bộ Y tế) đã ra thông báo về việc thu hồi 294 số tiếp nhận phiếu công bố mỹ phẩm của 7 doanh nghiệp. Nguyên nhân là các doanh nghiệp này đã có văn bản đề nghị thu hồi tự nguyện các số tiếp nhận.\n" +
//                        "\n" +
//                        "Cụ thể, Cục Quản lý Dược đã thu hồi số tiếp nhận phiếu công bố mỹ phẩm của các doanh nghiệp gồm: Công ty TNHH Đầu tư và phát triển HSD (địa chỉ ở Hưng Yên); Công ty TNHH Dịch vụ phần mềm Human Offshore Partners (địa chỉ ở quận Hà Đông, Hà Nội); Công ty TNHH liên minh HS (ở Hà Đông, Hà Nội); Công ty cổ phần đầu tư và thương mại Japan Connection (Hà Đông, Hà Nội); Công ty TNHH thương mại dịch vụ BB Việt Nam (Thanh Xuân, Hà Nội); Công ty TNHH đầu tư và phát triển 3M-INTEL (Gia Lâm, Hà Nội); Công ty TNHH HEBE VN.\n" +
//                        "\n" +
//                        "Ngày 30/5, đại diện Cục Quản lý Dược cho biết việc doanh nghiệp xin tự nguyện rút số tiếp nhận là bình thường và tuân thủ đúng quy định hiện hành.\n" +
//                        "\n" +
//                        "Trong năm 2024, Cục đã thu hồi khoảng 500 số tiếp nhận mỹ phẩm nhập khẩu theo đề nghị tự nguyện của doanh nghiệp, bên cạnh hơn 100 số bị thu hồi do phát hiện vi phạm hoặc sai sót.\n" +
//                        "\n" +
//                        "thu hoi my pham anh 1\n" +
//                        "Một số dòng sản phẩm phổ biến trên thị trường gồm sữa tắm, dầu gội dầu xả mang thương hiệu Dove, nước tẩy trang Bioderma. Ảnh: Thefaceholic.\n" +
//                        "Trong số các sản phẩm liên quan đến đề nghị thu hồi tự nguyện này có các loại mỹ phẩm phổ biến như kem chống nắng, sữa tắm, sữa rửa mặt, nước tẩy trang.\n" +
//                        "\n" +
//                        "Ví dụ, sản phẩm sữa tắm Dove Peony & Sweet cream body wash (số phiếu 215792/23/CBMP-QLD, cấp ngày 1/11/2023, sản xuất bởi Unilever Japan K.K., do Công ty TNHH Dịch vụ phần mềm Human Offshore Partners đưa ra thị trường).\n" +
//                        "\n" +
//                        "Sản phẩm kem chống nắng Shiseido ANESSA Perfect UV Suncreen Skincare Milk (số phiếu 156937/21/CBMP-QLD, cấp ngày 30/8/2021, do Shiseido Company, Limited (Nhật Bản) sản xuất, Công ty TNHH Đầu tư và phát triển HSD chịu trách nhiệm phân phối).\n" +
//                        "\n" +
//                        "Nước tẩy trang Bioderma Sebium H20 (số phiếu 216381/23/CBMP-QLD; nhà sản xuất NAOS LES LABORTOIRES, địa chỉ 505 Rue Pierre Berthier, 13290 Aix - en - Provence, Bouches du Rhône; ngày cấp 8/11/2023.\n" +
//                        "\n" +
//                        "Theo quy định, số tiếp nhận phiếu công bố mỹ phẩm là số do cơ quan quản lý nhà nước có thẩm quyền cấp để xác nhận rằng doanh nghiệp đã hoàn tất hồ sơ c",
//                "February 22, 2025",
//                    R.mipmap.img_sample
//        ));
//
//
//        return list;
//    }
//    public static List<NewsItem> convertToNewsItems(List<NewsDetailItem> detailList) {
//        List<NewsItem> items = new ArrayList<>();
//        for (NewsDetailItem detail : detailList) {
//            items.add(new NewsItem(
//                    detail.getTitle(),
//                    detail.getContent(),
//                    detail.getDate(),
//                    detail.getImageResId()
//            ));
//        }
//        return items;
//    }
//}
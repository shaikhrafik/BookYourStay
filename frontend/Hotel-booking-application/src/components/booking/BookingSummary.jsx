import React, { useState, useEffect } from "react"
import moment from "moment"
import Button from "react-bootstrap/Button"
import { useNavigate } from "react-router-dom"
import {useRazorpay} from 'react-razorpay'

const BookingSummary = ({ booking, payment, isFormValid, onConfirm }) => {
	const checkInDate = moment(booking.checkInDate)
	const checkOutDate = moment(booking.checkOutDate)
	const numberOfDays = checkOutDate.diff(checkInDate, "days")
	const [isBookingConfirmed, setIsBookingConfirmed] = useState(false)
	const [isProcessingPayment, setIsProcessingPayment] = useState(false)
	const navigate = useNavigate()
    
	const razorpay = useRazorpay();

	const handleclickOfCard = (item) => {
		console.log("Card clicked!", item);
	
		var options = {
		  key: "rzp_test_ILnuVxztFBgq6h",
		  key_secret: "NwaEsCIi3r9Pfi8no1ywuJjI",
		  currency: "INR",
		  amount: (item.rent)*100,
		  name: item.name,
		  description: item.owner,
		  handler: function (response) {
			setIsProcessingPayment(true)
		setTimeout(() => {
			setIsProcessingPayment(false)
			setIsBookingConfirmed(true)
			onConfirm()
		}, 1000)
			//toast("Your booking is Confirmed");
			navigate("/booking-success",{ state: { message: 'Booking successful!' } });
		  },
		  prefill: {
			name: "BookYourStay",
			email: "rafikshaikh10699@gmail.com",
			contact: "9370971534",
		  },
		  notes: {
			address: "IACSD CDAC pune, Maharastra",
		  },
		  theme: {
			color: "#3399cc",
		  },
		};
		var pay = new window.Razorpay(options);
		const resp = pay.open();
		console.log(resp);
	  };
	
	

	const handleConfirmBooking = () => {
		setIsProcessingPayment(true)
		setTimeout(() => {
			setIsProcessingPayment(false)
			setIsBookingConfirmed(true)
			onConfirm()
		}, 1000)
	}

	// useEffect(() => {
	// 	if (isBookingConfirmed) {
	// 		navigate("/booking-success")
	// 	}
	// }, [isBookingConfirmed, navigate])

	return (
		<div className="row">
			<div className="col-md-6"></div>
			<div className="card card-body mt-5">
				<h4 className="card-title hotel-color">Reservation Summary</h4>
				<p>
					Name: <strong>{booking.guestFullName}</strong>
				</p>
				<p>
					Email: <strong>{booking.guestEmail}</strong>
				</p>
				<p>
					Check-in Date: <strong>{moment(booking.checkInDate).format("MMM Do YYYY")}</strong>
				</p>
				<p>
					Check-out Date: <strong>{moment(booking.checkOutDate).format("MMM Do YYYY")}</strong>
				</p>
				<p>
					Number of Days Booked: <strong>{numberOfDays}</strong>
				</p>

				<div>
					<h5 className="hotel-color">Number of Guest</h5>
					<strong>
						Adult{booking.numOfAdults > 1 ? "s" : ""} : {booking.numOfAdults}
					</strong>
					<strong>
						<p>Children : {booking.numOfChildren}</p>
					</strong>
				</div>

				{payment > 0 ? (
					<>
						<p>
							Total payment: <strong>${payment}</strong>
						</p>

						{isFormValid && !isBookingConfirmed ? (
							<Button variant="success" onClick={()=>handleclickOfCard(booking)}>
								{isProcessingPayment ? (
									<>
										<span
											className="spinner-border spinner-border-sm mr-2"
											role="status"
											aria-hidden="true"></span>
										Booking Confirmed, redirecting to payment...
									</>
								) : (
									"Confirm Booking & proceed to payment"
								)}
							</Button>
						) : isBookingConfirmed ? (
							<div className="d-flex justify-content-center align-items-center">
								<div className="spinner-border text-primary" role="status">
									<span className="sr-only">Loading...</span>
								</div>
							</div>
						) : null}
					</>
				) : (
					<p className="text-danger">Check-out date must be after check-in date.</p>
				)}
			</div>
		</div>
	)
}

export default BookingSummary


// import React, { useState, useEffect } from "react"
// import moment from "moment"
// import Button from "react-bootstrap/Button"
// import { useNavigate } from "react-router-dom"

// const BookingSummary = ({ booking, payment, isFormValid, onConfirm }) => {
// 	const checkInDate = moment(booking.checkInDate)
// 	const checkOutDate = moment(booking.checkOutDate)
// 	const numberOfDays = checkOutDate.diff(checkInDate, "days")
// 	const [isBookingConfirmed, setIsBookingConfirmed] = useState(false)
// 	const [isProcessingPayment, setIsProcessingPayment] = useState(false)
// 	const navigate = useNavigate()

// 	const handleConfirmBooking = () => {
// 		setIsProcessingPayment(true)
// 		setTimeout(() => {
// 			setIsProcessingPayment(false)
// 			setIsBookingConfirmed(true)
// 			onConfirm()
// 		}, 1000)
// 	}

// 	useEffect(() => {
// 		if (isBookingConfirmed) {
// 			navigate("/booking-success")
// 		}
// 	}, [isBookingConfirmed, navigate])

// 	return (
// 		<div className="row">
// 			<div className="col-md-6"></div>
// 			<div className="card card-body mt-5">
// 				<h4 className="card-title hotel-color">Reservation Summary</h4>
// 				<p>
// 					Name: <strong>{booking.guestFullName}</strong>
// 				</p>
// 				<p>
// 					Email: <strong>{booking.guestEmail}</strong>
// 				</p>
// 				<p>
// 					Check-in Date: <strong>{moment(booking.checkInDate).format("MMM Do YYYY")}</strong>
// 				</p>
// 				<p>
// 					Check-out Date: <strong>{moment(booking.checkOutDate).format("MMM Do YYYY")}</strong>
// 				</p>
// 				<p>
// 					Number of Days Booked: <strong>{numberOfDays}</strong>
// 				</p>

// 				<div>
// 					<h5 className="hotel-color">Number of Guest</h5>
// 					<strong>
// 						Adult{booking.numOfAdults > 1 ? "s" : ""} : {booking.numOfAdults}
// 					</strong>
// 					<strong>
// 						<p>Children : {booking.numOfChildren}</p>
// 					</strong>
// 				</div>

// 				{payment > 0 ? (
// 					<>
// 						<p>
// 							Total payment: <strong>${payment}</strong>
// 						</p>

// 						{isFormValid && !isBookingConfirmed ? (
// 							<Button variant="success" onClick={handleConfirmBooking}>
// 								{isProcessingPayment ? (
// 									<>
// 										<span
// 											className="spinner-border spinner-border-sm mr-2"
// 											role="status"
// 											aria-hidden="true"></span>
// 										Booking Confirmed, redirecting to payment...
// 									</>
// 								) : (
// 									"Confirm Booking & proceed to payment"

									
// 								)}
// 							</Button>
// 						) : isBookingConfirmed ? (
// 							<div className="d-flex justify-content-center align-items-center">
// 								<div className="spinner-border text-primary" role="status">
// 									<span className="sr-only">Loading...</span>
// 								</div>
// 							</div>
// 						) : null}
// 					</>
// 				) : (
// 					<p className="text-danger">Check-out date must be after check-in date.</p>
// 				)}
// 			</div>
// 		</div>
// 	)
// }

// export default BookingSummary
